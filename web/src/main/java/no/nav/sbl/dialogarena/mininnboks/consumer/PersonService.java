package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.tjeneste.virksomhet.brukerprofil.v2.BrukerprofilV2;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;

import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public interface PersonService {

    Optional<String> hentEnhet();

    class Default implements PersonService {

        private final BrukerprofilV2 brukerprofilV2;

        public Default(BrukerprofilV2 brukerprofilV2) {
            this.brukerprofilV2 = brukerprofilV2;
        }

        @Override
        public Optional<String> hentEnhet() {
            try {
                String fnr = getSubjectHandler().getUid();
                WSPerson person = brukerprofilV2.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest().withPersonIdent(fnr)).getPerson();
                WSBruker bruker = (WSBruker) person;
                return of(bruker.getAnsvarligEnhet().getOrganisasjonselementID());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
