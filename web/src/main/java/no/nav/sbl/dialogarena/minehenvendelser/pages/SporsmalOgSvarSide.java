package no.nav.sbl.dialogarena.minehenvendelser.pages;

import java.util.List;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.NesteSide;
import no.nav.sbl.dialogarena.minehenvendelser.components.SendSporsmalPanel;
import no.nav.sbl.dialogarena.minehenvendelser.components.SporsmalBekreftelsePanel;
import no.nav.sbl.dialogarena.minehenvendelser.components.SporsmalOgSvarModel;
import no.nav.sbl.dialogarena.minehenvendelser.components.SporsmalOgSvarVM;
import no.nav.sbl.dialogarena.minehenvendelser.components.TemavelgerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.panel.Innboks;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

/**
 * Gir bruker mulighet til å sende inn spørsmål til NAV
 */
public class SporsmalOgSvarSide extends BasePage implements NesteSide {

    private static final String FODSELSNUMMER = "***REMOVED***";



    private enum Side {INNBOKS_BRUKER, TEMAVELGER, SEND_SPORSMAL, SPORMSMAL_BEKREFTELSE}
    final List<String> alleTema = asList("Uføre", "Sykepenger", "Tjenestebasert innskuddspensjon", "Annet");
    SporsmalOgSvarModel model = new SporsmalOgSvarModel(new SporsmalOgSvarVM().withTema("Ukjent"));
    IModel<Side> aktivSide = new CompoundPropertyModel<>(Side.INNBOKS_BRUKER);

    public SporsmalOgSvarSide() {
        Innboks innboks = new Innboks("innboks-bruker", FODSELSNUMMER);
        innboks.add(visibleIf(aktivSideEr(Side.INNBOKS_BRUKER)));
        TemavelgerPanel temavelger = new TemavelgerPanel("temavelger", alleTema, model, this);
        temavelger.add(visibleIf(aktivSideEr(Side.TEMAVELGER)));
        SendSporsmalPanel sendSporsmal = new SendSporsmalPanel("send-sporsmal", model, FODSELSNUMMER, this);
        sendSporsmal.add(visibleIf(aktivSideEr(Side.SEND_SPORSMAL)));
        SporsmalBekreftelsePanel sporsmalBekreftelse = new SporsmalBekreftelsePanel("sporsmal-bekreftelse", this);
        sporsmalBekreftelse.add(visibleIf(aktivSideEr(Side.SPORMSMAL_BEKREFTELSE)));
        AjaxLink innboksLink = new AjaxLink("innboks-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                aktivSide.setObject(Side.INNBOKS_BRUKER);
                target.add(SporsmalOgSvarSide.this);
            }
        };
        innboksLink.add(visibleIf(not(aktivSideEr(Side.INNBOKS_BRUKER))));
        AjaxLink skrivNyLink = new AjaxLink("skriv-ny-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                aktivSide.setObject(Side.TEMAVELGER);
                model.setTema("Ukjent");
                model.setFritekst(null);
                target.add(SporsmalOgSvarSide.this);
            }
        };
        skrivNyLink.add(visibleIf(aktivSideEr(Side.INNBOKS_BRUKER)));
        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(innboksLink, skrivNyLink);
        add(topBar, innboks, temavelger, sendSporsmal, sporsmalBekreftelse);
    }

    private IModel<Boolean> aktivSideEr(final Side side) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return aktivSide.getObject() == side;
            }
        };
    }

    @Override
    public void neste() {
        switch (aktivSide.getObject()) {
            case INNBOKS_BRUKER:
                aktivSide.setObject(Side.TEMAVELGER);
                break;
            case TEMAVELGER:
                aktivSide.setObject(Side.SEND_SPORSMAL);
                break;
            case SEND_SPORSMAL:
                aktivSide.setObject(Side.SPORMSMAL_BEKREFTELSE);
                break;
            case SPORMSMAL_BEKREFTELSE:
                aktivSide.setObject(Side.INNBOKS_BRUKER);
                break;
            default:
                throw new RuntimeException("Kjenner ikke til siden " + aktivSide.getObject());
        }
    }

}
