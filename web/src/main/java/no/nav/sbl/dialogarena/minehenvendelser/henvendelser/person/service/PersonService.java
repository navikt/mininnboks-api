package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.service;


import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.Person;

public interface PersonService {
    Person hentPerson(String ident);
    void oppdaterPerson(Person person);
}