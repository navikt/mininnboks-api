package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.service.PersonService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.security.Brukerkontekst;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class SendSporsmalPage extends BasePage implements SideNavigerer {


    private enum Side {TEMAVELGER, SAMTYKKE, SEND_SPORSMAL, SPORMSMAL_BEKREFTELSE}

    @Inject
    HenvendelseService henvendelseService;

    @Inject
    PersonService personService;

    @Inject
    Brukerkontekst brukerkontekst;

    IModel<Side> aktivSide = new Model<>(Side.values()[0]);
    CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(new Sporsmal());

    public SendSporsmalPage() {

        Sporsmal spsm = model.getObject();
        model.setObject(spsm);

        TemavelgerPanel temavelger = new TemavelgerPanel("temavelger", asList(Tema.values()), model, this);
        temavelger.add(visibleIf(aktivSideEr(Side.TEMAVELGER)));

        SamtykkePanel avgiSamtykke = new SamtykkePanel("avgi-samtykke", this);
        avgiSamtykke.add(visibleIf(aktivSideEr(Side.SAMTYKKE)));

        SendSporsmalPanel sendSporsmal = new SendSporsmalPanel("send-sporsmal", model, this, henvendelseService);
        sendSporsmal.add(visibleIf(aktivSideEr(Side.SEND_SPORSMAL)));

        SporsmalBekreftelsePanel sporsmalBekreftelse = new SporsmalBekreftelsePanel("sporsmal-bekreftelse");
        sporsmalBekreftelse.add(visibleIf(aktivSideEr(Side.SPORMSMAL_BEKREFTELSE)));

        add(temavelger, avgiSamtykke, sendSporsmal, sporsmalBekreftelse);

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
        if (aktivSide.getObject() == Side.SAMTYKKE) {
            Person person = personService.hentPerson(brukerkontekst.getBrukerId());
            if (person.getPreferanser().isElektroniskSamtykke()) {
                neste();
            }
        }
    }
}
