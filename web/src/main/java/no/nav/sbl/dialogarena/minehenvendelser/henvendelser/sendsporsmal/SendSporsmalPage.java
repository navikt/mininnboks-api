package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.HenvendelseService;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class SendSporsmalPage extends BasePage implements SideNavigerer {


    private enum Side {TEMAVELGER, SEND_SPORSMAL, SPORMSMAL_BEKREFTELSE}

    @Inject
    HenvendelseService henvendelseService;

    IModel<Side> aktivSide = new Model<>(Side.values()[0]);
    CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(new Sporsmal());

    public SendSporsmalPage() {

        Sporsmal spsm = model.getObject();
        model.setObject(spsm);

        TemavelgerPanel temavelger = new TemavelgerPanel("temavelger", asList(Tema.values()), model, this);
        temavelger.add(visibleIf(aktivSideEr(Side.TEMAVELGER)));

        SendSporsmalPanel sendSporsmal = new SendSporsmalPanel("send-sporsmal", model, this, henvendelseService);
        sendSporsmal.add(visibleIf(aktivSideEr(Side.SEND_SPORSMAL)));

        SporsmalBekreftelsePanel sporsmalBekreftelse = new SporsmalBekreftelsePanel("sporsmal-bekreftelse");
        sporsmalBekreftelse.add(visibleIf(aktivSideEr(Side.SPORMSMAL_BEKREFTELSE)));

        add(temavelger, sendSporsmal, sporsmalBekreftelse);

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
