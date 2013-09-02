package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import java.util.List;
import javax.inject.Inject;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.MeldingService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class SendSporsmalPage extends BasePage implements SideNavigerer {


    private enum Side {TEMAVELGER, SEND_SPORSMAL, SPORMSMAL_BEKREFTELSE}

    @Inject
    MeldingService meldingService;

    IModel<Side> aktivSide = new Model<>(Side.values()[0]);
    final List<String> alleTema = asList("Uføre", "Sykepenger", "Tjenestebasert innskuddspensjon", "Annet");
    CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(new Sporsmal());

    public SendSporsmalPage() {

        TemavelgerPanel temavelger = new TemavelgerPanel("temavelger", alleTema, model, this);
        temavelger.add(visibleIf(aktivSideEr(Side.TEMAVELGER)));

        SendSporsmalPanel sendSporsmal = new SendSporsmalPanel("send-sporsmal", model, this, meldingService);
        sendSporsmal.add(visibleIf(aktivSideEr(Side.SEND_SPORSMAL)));

        SporsmalBekreftelsePanel sporsmalBekreftelse = new SporsmalBekreftelsePanel("sporsmal-bekreftelse", model);
        sporsmalBekreftelse.add(visibleIf(aktivSideEr(Side.SPORMSMAL_BEKREFTELSE)));

        Link innboksLink = new Link("innboks-link") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        };

        add(innboksLink, temavelger, sendSporsmal, sporsmalBekreftelse);

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
        aktivSide.setObject(Side.values()[aktivSide.getObject().ordinal() + 1]);
    }

}
