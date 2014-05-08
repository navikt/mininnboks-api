package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.VelgTemaPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.TraadVM.erLest;

public class Innboks extends BasePage {

    public static final String VALGT_HENVENDELSE = "hendelser.valgt_henvendelse";
    public static final String OPPDATER_HENVENDELSER = "hendelser.oppdater_henvendelser";

    @Inject
    private HenvendelseService service;

    private InnboksVM innboksVM;

    public Innboks() {
        innboksVM = new InnboksVM(service.hentAlleHenvendelser(innloggetBruker()));
        setDefaultModel(new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(new Link<Void>("skriv-ny") {
            @Override
            public void onClick() {
                setResponsePage(VelgTemaPage.class);
            }
        });

        ListView traader = new ListView<TraadVM>("traader", innboksVM.getTraader()) {
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
        tomInnboks.add(hasCssClassIf("ingen-meldinger", Model.of(!innboksVM.ingenHenvendelser().getObject())));

        add(topBar, traader, tomInnboks);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

    @RunOnEvents(OPPDATER_HENVENDELSER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        innboksVM.oppdaterHenvendelserFra(service.hentAlleHenvendelser(innloggetBruker()));
        target.add(this);
    }

    private static String innloggetBruker() {
        return SubjectHandler.getSubjectHandler().getUid();
    }
}
