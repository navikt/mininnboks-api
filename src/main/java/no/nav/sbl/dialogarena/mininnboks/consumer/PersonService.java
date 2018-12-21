package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonidenter;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;

import java.util.Optional;

import static java.util.Optional.of;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

public interface PersonService {

    Optional<String> hentEnhet();

    class Default implements PersonService {

        static final WSPersonidenter identtype = new WSPersonidenter()
                .withKodeRef("http://nav.no/kodeverk/Term/Personidenter/FNR/nb/F_c3_b8dselnummer?v=1")
                .withValue("FNR");
        private final BrukerprofilV3 brukerprofilV3;

        public Default(BrukerprofilV3 brukerprofilV3) {
            this.brukerprofilV3 = brukerprofilV3;
        }

        @Override
        public Optional<String> hentEnhet() {
            try {
                String fnr = getSubjectHandler().getUid();
                WSNorskIdent ident = new WSNorskIdent().withType(identtype).withIdent(fnr);
                WSHentKontaktinformasjonOgPreferanserRequest kontaktRequest = new WSHentKontaktinformasjonOgPreferanserRequest().withIdent(ident);
                WSPerson person = brukerprofilV3.hentKontaktinformasjonOgPreferanser(kontaktRequest).getBruker();
                WSBruker bruker = (WSBruker) person;
                return of(bruker.getAnsvarligEnhet().getOrganisasjonselementId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
