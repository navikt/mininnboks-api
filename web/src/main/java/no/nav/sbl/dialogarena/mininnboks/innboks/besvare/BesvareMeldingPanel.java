package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class BesvareMeldingPanel extends GenericPanel<BesvareMeldingPanel.Svar> implements Serializable {

    public BesvareMeldingPanel(String id, final TraadVM traadVM, final Component traadListItemParent) {
        super(id, new CompoundPropertyModel<>(new Svar(traadVM)));
        setOutputMarkupId(true);

        AjaxLink<TraadVM> besvareKnapp = new AjaxLink<TraadVM>("besvareKnapp", Model.of(traadVM)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModelObject().besvareModus.setObject(true);
                target.add(BesvareMeldingPanel.this);
            }
        };

        SvarForm form = new SvarForm("form", getModel(), traadVM, this, traadListItemParent);

        KvitteringPanel kvittering = new KvitteringPanel("kvittering");

        add(visibleIf(not(traadVM.lukket)));
        besvareKnapp.add(visibleIf(both(not(traadVM.besvareModus)).and(not(traadVM.lukket)).and(traadVM.kanBesvares())));
        form.add(visibleIf(both(traadVM.besvareModus).and(not(traadVM.meldingBesvart))));
        kvittering.add(visibleIf(traadVM.meldingBesvart));

        add(besvareKnapp, form, kvittering);
    }

    public static class Svar extends EnhancedTextAreaModel {
        public final String traadId;
        public final Temagruppe temagruppe;
        private final TraadVM traadVM;

        public Svar(TraadVM traadVM) {
            this.traadId = traadVM.id;
            this.temagruppe = traadVM.temagruppe;
            this.traadVM = traadVM;
        }

        public String getEksternAktor() {
            return traadVM.nyesteHenvendelse().getObject().eksternAktor;
        }

        public String getTilknyttetEnhet() {
            return traadVM.nyesteHenvendelse().getObject().tilknyttetEnhet;
        }

        public String getFritekst() {
            return text;
        }
    }
}
