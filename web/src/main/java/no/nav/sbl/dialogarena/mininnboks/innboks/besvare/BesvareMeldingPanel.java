package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SVAR_SBL_INNGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.henvendelseTemagruppeKey;
import static org.joda.time.DateTime.now;

public class BesvareMeldingPanel extends GenericPanel<BesvareMeldingPanel.Svar> implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(BesvareMeldingPanel.class);

    @Inject
    private HenvendelseService henvendelseService;

    private final IModel<Boolean> meldingBesvart = Model.of(false);

    private final AriaFeedbackPanel feedbackPanel;

    public BesvareMeldingPanel(String id, final TraadVM traadVM, final Component... oppdaterbareKomponenter) {
        super(id, new CompoundPropertyModel<>(new Svar(traadVM.id, traadVM.temagruppe)));
        setOutputMarkupId(true);

        add(visibleIf(not(traadVM.lukket)));

        AjaxLink<TraadVM> besvareKnapp = new AjaxLink<TraadVM>("besvareKnapp", Model.of(traadVM)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModelObject().besvareModus.setObject(true);
                target.add(BesvareMeldingPanel.this);
            }
        };
        besvareKnapp.add(visibleIf(both(not(traadVM.besvareModus)).and(not(traadVM.lukket)).and(traadVM.kanBesvares())));

        Form<Svar> form = new Form<>("form", getModel());
        form.add(new EnhancedTextArea("fritekst", getModel()));

        feedbackPanel = new AriaFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new AjaxSubmitLink("sendSvar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sendSvar(target, traadVM, oppdaterbareKomponenter);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });
        form.add(new AjaxLink("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                traadVM.besvareModus.setObject(false);
                target.add(BesvareMeldingPanel.this);
            }
        });

        WebMarkupContainer besvareContainer = new WebMarkupContainer("besvareContainer");
        besvareContainer.add(visibleIf(both(traadVM.besvareModus).and(not(meldingBesvart))));

        besvareContainer.add(
                new Label("temagruppe", new ResourceModel(henvendelseTemagruppeKey(getModelObject().temagruppe))),
                form);

        KvitteringPanel kvittering = new KvitteringPanel("kvittering");
        kvittering.add(visibleIf(meldingBesvart));

        add(besvareKnapp, besvareContainer, kvittering);
    }

    private void sendSvar(AjaxRequestTarget target, TraadVM traadVM, Component... oppdaterbareKomponenter) {
        try {
            henvendelseService.sendSvar(
                    getModelObject().getFritekst(),
                    getModelObject().temagruppe,
                    getModelObject().traadId,
                    getSubjectHandler().getUid());

            traadVM.henvendelser.add(0, lagMidlertidigHenvendelse());

            meldingBesvart.setObject(true);

            target.add(BesvareMeldingPanel.this);
            target.add(oppdaterbareKomponenter);
        } catch (Exception e) {
            LOG.debug("Feil ved innsending av svar", e);
            error(getString("besvare.feilmelding.innsending"));
            target.add(feedbackPanel);
        }
    }

    private Henvendelse lagMidlertidigHenvendelse() {
        Henvendelse henvendelse = new Henvendelse("midlertidig");
        henvendelse.traadId = getModelObject().traadId;
        henvendelse.temagruppe = getModelObject().temagruppe;
        henvendelse.fritekst = getModelObject().getFritekst();
        henvendelse.type = SVAR_SBL_INNGAAENDE;
        henvendelse.opprettet = now();
        henvendelse.markerSomLest();

        return henvendelse;
    }

    public static class Svar extends EnhancedTextAreaModel {
        public final String traadId;
        public final Temagruppe temagruppe;

        public Svar(String traadId, Temagruppe temagruppe) {
            this.traadId = traadId;
            this.temagruppe = temagruppe;
        }

        public String getFritekst() {
            return text;
        }

    }
}
