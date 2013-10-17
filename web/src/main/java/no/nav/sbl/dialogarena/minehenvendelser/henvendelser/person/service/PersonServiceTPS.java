package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.service;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.HentBrukerprofilConsumer;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.OppdaterBrukerprofilConsumer;

public class PersonServiceTPS implements PersonService {

    private final HentBrukerprofilConsumer hentConsumer;
    private final OppdaterBrukerprofilConsumer oppdaterConsumer;

    public PersonServiceTPS(HentBrukerprofilConsumer hentBrukerprofilConsumer, OppdaterBrukerprofilConsumer oppdaterBrukerprofilConsumer) {
        this.hentConsumer = hentBrukerprofilConsumer;
        this.oppdaterConsumer = oppdaterBrukerprofilConsumer;
    }

    @Override
    public Person hentPerson(final String ident) {
        return hentConsumer.hentPerson(ident);
    }

    @Override
    public void oppdaterPerson(Person person) {
        oppdaterConsumer.oppdaterPerson(person);
    }
}