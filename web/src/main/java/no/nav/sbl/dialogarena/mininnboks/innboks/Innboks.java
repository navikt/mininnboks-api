package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.VelgTemaPage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.erLest;
import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.tilTraader;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class Innboks extends BasePage {

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

                item.add(hasCssClassIf("lest", erLest(item.getModelObject().henvendelser)));
                item.add(hasCssClassIf("closed", item.getModelObject().lukket));

                item.add(new AjaxLink<Void>("flipp") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        traadClickBehaviour(item, target);
                    }
                });
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        if (item.getModelObject().lukket.getObject()) {
                            traadClickBehaviour(item, target);
                        }
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

    private void traadClickBehaviour(ListItem<TraadVM> item, AjaxRequestTarget target) {
        TraadVM traadVM = item.getModelObject();
        if (!erLest(traadVM.henvendelser).getObject()) {
            traadVM.markerSomLest(service);
            target.appendJavaScript("Innboks.markerSomLest('" + item.getMarkupId() + "');");
        }
        traadVM.lukket.setObject(!traadVM.lukket.getObject());
        target.appendJavaScript("Innboks.toggleTraad('" + item.getMarkupId() + "');");
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

    private static String innloggetBruker() {
        return SubjectHandler.getSubjectHandler().getUid();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

}
