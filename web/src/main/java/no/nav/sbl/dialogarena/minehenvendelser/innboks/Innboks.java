package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.VelgTemaPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.TraadVM.erLest;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.TraadVM.tilTraader;

public class Innboks extends BasePage {

    public static final String OPPDATER_HENVENDELSER = "hendelser.oppdater_henvendelser";

    @Inject
    private HenvendelseService service;

    private List<Henvendelse> henvendelser;
    private List<TraadVM> traader;

    public Innboks() {
        oppdaterHenvendelserFra(service.hentAlleHenvendelser(innloggetBruker()));

        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(new Link<Void>("skriv-ny") {
            @Override
            public void onClick() {
                setResponsePage(VelgTemaPage.class);
            }
        });

        ListView traadListe = new ListView<TraadVM>("traader", traader) {
            @Override
            protected void populateItem(final ListItem<TraadVM> item) {
                item.setOutputMarkupId(true);

                final TraadVM traadVM = item.getModelObject();
                item.add(hasCssClassIf("closed", traadVM.lukket));
                item.add(hasCssClassIf("lest", erLest(traadVM.henvendelser)));

                item.add(new AjaxLink<Void>("flipp") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (!erLest(traadVM.henvendelser).getObject()) {
                            traadVM.markerSomLest(service);
                        }

                        if (traadVM.lukket.getObject()) {
                            traadVM.lukket.setObject(false);
                        } else {
                            traadVM.lukket.setObject(true);
                        }

                        target.add(item);
                    }
                });

                item.add(new NyesteMeldingPanel("nyeste-melding", item.getModel()));
                item.add(new TidligereMeldingerPanel("tidligere-meldinger", item.getModel()));
            }
        };

        WebMarkupContainer tomInnboks = new WebMarkupContainer("tom-innboks");
        tomInnboks.add(new Label("tom-innboks-tekst", new StringResourceModel("innboks.tom-innboks-melding", this, null)));
        tomInnboks.add(hasCssClassIf("ingen-meldinger", Model.of(!ingenHenvendelser().getObject())));

        add(topBar, traadListe, tomInnboks);
    }

    private void oppdaterHenvendelserFra(List<Henvendelse> henvendelser) {
        this.henvendelser = henvendelser;
        this.traader = tilTraader(henvendelser);
    }

    private IModel<Boolean> ingenHenvendelser() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelser.size() == 0;
            }
        };
    }

    @RunOnEvents(OPPDATER_HENVENDELSER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        oppdaterHenvendelserFra(service.hentAlleHenvendelser(innloggetBruker()));
        target.add(this);
    }

    private static String innloggetBruker() {
        return SubjectHandler.getSubjectHandler().getUid();
    }
}
