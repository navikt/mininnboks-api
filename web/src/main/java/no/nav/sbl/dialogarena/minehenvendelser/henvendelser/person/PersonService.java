package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.HentBrukerprofilConsumer;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.OppdaterBrukerprofilConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface PersonService {

    Logger LOG = LoggerFactory.getLogger(PersonService.class);

    Person hentPerson(String ident);

    void oppdaterPerson(Person person);


    class Default implements PersonService {

        private final HentBrukerprofilConsumer hentConsumer;

        private final OppdaterBrukerprofilConsumer oppdaterConsumer;

        public Default(HentBrukerprofilConsumer hentBrukerprofilConsumer, OppdaterBrukerprofilConsumer oppdaterBrukerprofilConsumer) {
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
}