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

public class SendSporsmalWizard extends BasePage implements Stegnavigerer {

    private enum Steg {VELG_TEMA, SAMTYKK, SEND, KVITTERING}

    @Inject
    HenvendelseService henvendelseService;

    @Inject
    PersonService personService;

    @Inject
    Brukerkontekst brukerkontekst;

    IModel<Steg> aktivtSteg = new Model<>(Steg.values()[0]);
    CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(new Sporsmal());

    public SendSporsmalWizard() {

        Sporsmal spsm = model.getObject();
        model.setObject(spsm);

        VelgTemaPanel velgTema = new VelgTemaPanel("velg-tema", model, this);
        velgTema.add(visibleIf(aktivtStegEr(Steg.VELG_TEMA)));

        SamtykkePanel avgiSamtykke = new SamtykkePanel("avgi-samtykke", this);
        avgiSamtykke.add(visibleIf(aktivtStegEr(Steg.SAMTYKK)));

        SendPanel sendSporsmal = new SendPanel("send-sporsmal", model, this, henvendelseService);
        sendSporsmal.add(visibleIf(aktivtStegEr(Steg.SEND)));

        KvitteringPanel kvittering = new KvitteringPanel("kvittering");
        kvittering.add(visibleIf(aktivtStegEr(Steg.KVITTERING)));

        add(velgTema, avgiSamtykke, sendSporsmal, kvittering);

    }

    private IModel<Boolean> aktivtStegEr(final Steg steg) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return aktivtSteg.getObject() == steg;
            }
        };
    }

    @Override
    public void neste() {
        aktivtSteg.setObject(Steg.values()[aktivtSteg.getObject().ordinal() + 1]);
        if (aktivtSteg.getObject() == Steg.SAMTYKK) {
            Person person = personService.hentPerson(brukerkontekst.getBrukerId());
            if (person.getPreferanser().isElektroniskSamtykke()) {
                neste();
            }
        }
    }
}
