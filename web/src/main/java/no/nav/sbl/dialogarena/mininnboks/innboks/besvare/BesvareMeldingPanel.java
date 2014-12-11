package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.henvendelseTemagruppeKey;
import static no.nav.sbl.dialogarena.mininnboks.utils.Event.HENVENDELSE_BESVART;

public class BesvareMeldingPanel extends GenericPanel<BesvareMeldingPanel.Svar> {

    private static final Logger LOG = LoggerFactory.getLogger(BesvareMeldingPanel.class);

    @Inject
    private HenvendelseService henvendelseService;

    public BesvareMeldingPanel(String id, final IModel<TraadVM> traadVM) {
        super(id, new CompoundPropertyModel<>(new Svar()));
        setOutputMarkupId(true);

        add(visibleIf(both(not(traadVM.getObject().lukket)).and(traadVM.getObject().kanBesvares())));

        AjaxLink<TraadVM> besvareKnapp = new AjaxLink<TraadVM>("besvareKnapp", traadVM) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModelObject().besvareModus.setObject(true);
                target.add(BesvareMeldingPanel.this);
            }
        };
        besvareKnapp.add(visibleIf(both(not(traadVM.getObject().besvareModus)).and(not(traadVM.getObject().lukket))));

        WebMarkupContainer besvareContainer = new WebMarkupContainer("besvareContainer");
        besvareContainer.add(visibleIf(traadVM.getObject().besvareModus));

        Form<Svar> form = new Form<>("form", getModel());
        form.add(new EnhancedTextArea("fritekst", getModel()));
        form.add(new AjaxSubmitLink("sendSvar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    henvendelseService.sendSvar(
                            getModelObject().getFritekst(),
                            traadVM.getObject().temagruppe,
                            traadVM.getObject().id,
                            getSubjectHandler().getUid());

                    send(getPage(), Broadcast.BREADTH, HENVENDELSE_BESVART);
                } catch (Exception e) {
                    LOG.debug("Feil ved innsending av svar", e);
                }
            }
        });
        form.add(new AjaxLink("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                traadVM.getObject().besvareModus.setObject(false);
                target.add(BesvareMeldingPanel.this);
            }
        });

        besvareContainer.add(
                new Label("temagruppe", new ResourceModel(henvendelseTemagruppeKey(traadVM.getObject().temagruppe))),
                form);

        add(besvareKnapp, besvareContainer);
    }

    public static class Svar extends EnhancedTextAreaModel {
        public String getFritekst() {
            return text;
        }

    }
}
