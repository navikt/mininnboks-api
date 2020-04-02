package no.nav.sbl.dialogarena.mininnboks.consumer;

import lombok.SneakyThrows;
import no.nav.common.auth.SubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonidenter;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAuthorizedException;
import java.util.Optional;

import static java.util.Optional.of;

public interface PersonService {

    Optional<String> hentEnhet();
    Optional<String> hentGeografiskTilknytning();

    class Default implements PersonService {
        static final Logger logger = LoggerFactory.getLogger(PersonService.class);

        static final WSPersonidenter brukerprofilIdentType = new WSPersonidenter()
                .withKodeRef("http://nav.no/kodeverk/Term/Personidenter/FNR/nb/F_c3_b8dselnummer?v=1")
                .withValue("FNR");
        static final Personidenter personV3IdentType = new Personidenter()
                .withValue("FNR");
        private final BrukerprofilV3 brukerprofilV3;
        private final PersonV3 personV3;

        public Default(BrukerprofilV3 brukerprofilV3, PersonV3 personV3) {
            this.brukerprofilV3 = brukerprofilV3;
            this.personV3 = personV3;
        }

        @Override
        @SneakyThrows
        public Optional<String> hentEnhet() {
            String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
            WSNorskIdent ident = new WSNorskIdent().withType(brukerprofilIdentType).withIdent(fnr);
            WSHentKontaktinformasjonOgPreferanserRequest kontaktRequest = new WSHentKontaktinformasjonOgPreferanserRequest().withIdent(ident);
            WSPerson person = brukerprofilV3.hentKontaktinformasjonOgPreferanser(kontaktRequest).getBruker();
            WSBruker bruker = (WSBruker) person;
            return of(bruker.getAnsvarligEnhet().getOrganisasjonselementId());
        }

        public Optional<String> hentGeografiskTilknytning() {
            String fnr = SubjectHandler.getIdent().orElseThrow(() -> new NotAuthorizedException("Fant ikke brukers OIDC-token"));
            HentGeografiskTilknytningRequest request = new HentGeografiskTilknytningRequest().withAktoer(lagAktoer(fnr));

            HentGeografiskTilknytningResponse response;
            try {
                response = personV3.hentGeografiskTilknytning(request);
            } catch (HentGeografiskTilknytningSikkerhetsbegrensing hentGeografiskTilknytningSikkerhetsbegrensing) {
                logger.info("HentGeografiskTilknytningSikkerhetsbegrensing ved kall på hentGeografiskTilknyttning", hentGeografiskTilknytningSikkerhetsbegrensing);
                return Optional.empty();
            } catch (HentGeografiskTilknytningPersonIkkeFunnet hentGeografiskTilknytningPersonIkkeFunnet) {
                logger.info("HentGeografiskTilknytningPersonIkkeFunnet ved kall på hentGeografiskTilknyttning", hentGeografiskTilknytningPersonIkkeFunnet);
                return Optional.empty();
            }

            return Optional.of(response)
                    .map(HentGeografiskTilknytningResponse::getGeografiskTilknytning)
                    .map(GeografiskTilknytning::getGeografiskTilknytning);
        }

        private PersonIdent lagAktoer(String ident) {
            return new PersonIdent().withIdent(new NorskIdent().withType(personV3IdentType).withIdent(ident));
        }
    }
}
