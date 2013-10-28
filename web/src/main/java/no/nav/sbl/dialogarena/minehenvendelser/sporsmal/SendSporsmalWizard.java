package no.nav.sbl.dialogarena.minehenvendelser.sporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.person.consumer.Person;
import no.nav.sbl.dialogarena.minehenvendelser.person.service.PersonService;
import no.nav.sbl.dialogarena.minehenvendelser.security.Brukerkontekst;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.kvittering.KvitteringPanel;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.samtykke.SamtykkePanel;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.send.SendPanel;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.VelgTemaPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class SendSporsmalWizard extends BasePage implements SideNavigerer {


    private enum Side {TEMAVELGER, SAMTYKKE, SEND_SPORSMAL, SPORMSMAL_BEKREFTELSE}

    @Inject
    HenvendelseService henvendelseService;

    @Inject
    PersonService personService;

    @Inject
    Brukerkontekst brukerkontekst;

    IModel<Side> aktivSide = new Model<>(Side.values()[0]);
    CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(new Sporsmal());

    public SendSporsmalWizard() {

        Sporsmal spsm = model.getObject();
        model.setObject(spsm);

        VelgTemaPanel temavelger = new VelgTemaPanel("temavelger", model, this);
        temavelger.add(visibleIf(aktivSideEr(Side.TEMAVELGER)));

        SamtykkePanel avgiSamtykke = new SamtykkePanel("avgi-samtykke", this);
        avgiSamtykke.add(visibleIf(aktivSideEr(Side.SAMTYKKE)));

        SendPanel sendSporsmal = new SendPanel("send-sporsmal", model, this, henvendelseService);
        sendSporsmal.add(visibleIf(aktivSideEr(Side.SEND_SPORSMAL)));

        KvitteringPanel sporsmalBekreftelse = new KvitteringPanel("sporsmal-bekreftelse");
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
