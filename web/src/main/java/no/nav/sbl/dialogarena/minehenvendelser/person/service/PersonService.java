package no.nav.sbl.dialogarena.minehenvendelser.person.service;


import no.nav.sbl.dialogarena.minehenvendelser.person.consumer.Person;

public interface PersonService {
    Person hentPerson(String ident);
    void oppdaterPerson(Person person);
}