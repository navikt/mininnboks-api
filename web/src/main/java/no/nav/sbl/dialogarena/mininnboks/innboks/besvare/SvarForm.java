package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SVAR_SBL_INNGAAENDE;
import static org.joda.time.DateTime.now;

public class SvarForm extends Form<BesvareMeldingPanel.Svar> {

    private static final Logger LOG = LoggerFactory.getLogger(BesvareMeldingPanel.class);

    @Inject
    private HenvendelseService henvendelseService;

    private final AriaFeedbackPanel feedbackPanel;

    public SvarForm(String id, IModel<BesvareMeldingPanel.Svar> model, final TraadVM traadVM, final Component... oppdaterbareKomponenter) {
        super(id, model);

        add(new EnhancedTextArea("fritekst", getModel()));

        feedbackPanel = new AriaFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new AjaxButton("sendSvar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sendSvar(target, traadVM, oppdaterbareKomponenter);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });
        add(new AjaxLink("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                traadVM.besvareModus.setObject(false);
                target.add(oppdaterbareKomponenter);
            }
        });

    }

    private void sendSvar(AjaxRequestTarget target, TraadVM traadVM, Component... oppdaterbareKomponenter) {
        Henvendelse henvendelse = opprettHenvendelse();
        try {
            henvendelseService.sendSvar(henvendelse, getSubjectHandler().getUid());

            traadVM.henvendelser.add(0, henvendelse);
            traadVM.meldingBesvart.setObject(true);

            target.add(oppdaterbareKomponenter);
        } catch (Exception e) {
            LOG.debug("Feil ved innsending av svar", e);
            error(getString("besvare.feilmelding.innsending"));
            target.add(feedbackPanel);
        }
    }

    private Henvendelse opprettHenvendelse() {
        Henvendelse henvendelse = new Henvendelse(getModelObject().getFritekst(), getModelObject().temagruppe);
        henvendelse.id = "midlertidig";
        henvendelse.traadId = getModelObject().traadId;
        henvendelse.eksternAktor = getModelObject().getEksternAktor();
        henvendelse.tilknyttetEnhet = getModelObject().getTilknyttetEnhet();
        henvendelse.type = SVAR_SBL_INNGAAENDE;
        henvendelse.opprettet = now();
        henvendelse.markerSomLest();

        return henvendelse;
    }
}
