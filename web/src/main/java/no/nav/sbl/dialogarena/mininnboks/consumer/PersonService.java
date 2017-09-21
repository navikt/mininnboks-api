package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSGeografi;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSFinnNAVKontorRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSFinnNAVKontorResponse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPersonidenter;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentGeografiskTilknytningResponse;


import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static org.slf4j.LoggerFactory.getLogger;

public interface PersonService {

    Optional<String> finnNavKontor();

    class Default implements PersonService {

        private static final org.slf4j.Logger logger = getLogger(PersonService.class);

        static final WSPersonidenter identtype = new WSPersonidenter()
                .withKodeRef("http://nav.no/kodeverk/Term/Personidenter/FNR/nb/F_c3_b8dselnummer?v=1")
                .withValue("FNR");
        private final PersonV3 personV3;
        private final OrganisasjonEnhetV2 organisasjonEnhetV2;

        public Default(PersonV3 personV3, OrganisasjonEnhetV2 organisasjonEnhetV2) {
            this.personV3 = personV3;
            this.organisasjonEnhetV2 = organisasjonEnhetV2;
        }

        @Override
        public Optional<String> finnNavKontor() {
            try {
                String fnr = getSubjectHandler().getUid();
                WSNorskIdent ident = new WSNorskIdent().withType(identtype).withIdent(fnr);
                WSPersonIdent personIdent = new WSPersonIdent().withIdent(ident);

                WSHentGeografiskTilknytningResponse geografiskTilknytningResponse =
                        personV3.hentGeografiskTilknytning(new WSHentGeografiskTilknytningRequest().withAktoer(personIdent));


                WSFinnNAVKontorResponse finnNAVKontorResponse =
                        organisasjonEnhetV2.finnNAVKontor(new WSFinnNAVKontorRequest()
                                .withGeografiskTilknytning(new WSGeografi().withValue(
                                        geografiskTilknytningResponse.getGeografiskTilknytning().getGeografiskTilknytning())
                                )
                        );
                return of(finnNAVKontorResponse.getNAVKontor().getEnhetId());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
