package no.nav.sbl.dialogarena.minehenvendelser.person.service;

import no.nav.sbl.dialogarena.minehenvendelser.person.consumer.Person;
import no.nav.sbl.dialogarena.minehenvendelser.person.consumer.HentBrukerProfilConsumer;
import no.nav.sbl.dialogarena.minehenvendelser.person.consumer.OppdaterBrukerprofilConsumer;

public class PersonServiceTPS implements PersonService {

    private final HentBrukerProfilConsumer hentConsumer;
    private final OppdaterBrukerprofilConsumer oppdaterConsumer;

    public PersonServiceTPS(HentBrukerProfilConsumer hentBrukerProfilConsumer, OppdaterBrukerprofilConsumer oppdaterBrukerprofilConsumer) {
        this.hentConsumer = hentBrukerProfilConsumer;
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