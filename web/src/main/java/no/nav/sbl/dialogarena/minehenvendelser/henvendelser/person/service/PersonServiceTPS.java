package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.service;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.HentBrukerprofilPerson;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.OppdaterBrukerprofilConsumer;

public class PersonServiceTPS implements PersonService {

    private final HentBrukerprofilPerson hentConsumer;
    private final OppdaterBrukerprofilConsumer oppdaterConsumer;

    public PersonServiceTPS(HentBrukerprofilPerson hentBrukerprofilPerson, OppdaterBrukerprofilConsumer oppdaterBrukerprofilConsumer) {
        this.hentConsumer = hentBrukerprofilPerson;
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