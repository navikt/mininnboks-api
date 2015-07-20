package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLEPost;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLElektroniskAdresse;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLElektroniskKommunikasjonskanal;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;
import no.nav.tjeneste.virksomhet.person.v2.PersonV2;
import org.apache.commons.collections15.Transformer;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.isA;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.lang.option.Optional.optional;

public interface PersonService {

    String hentEpostadresse() throws Exception;

    Optional<String> hentEnhet();

    class Default implements PersonService {

        private final BrukerprofilPortType brukerprofil;
        private final PersonV2 personV2;

        public Default(BrukerprofilPortType brukerprofilPortType, PersonV2 personV2) {
            this.brukerprofil = brukerprofilPortType;
            this.personV2 = personV2;
        }

        @Override
        public String hentEpostadresse() throws Exception {
            String brukerId = SubjectHandler.getSubjectHandler().getUid();
            XMLHentKontaktinformasjonOgPreferanserRequest request = new XMLHentKontaktinformasjonOgPreferanserRequest().withIdent(brukerId);

            XMLHentKontaktinformasjonOgPreferanserResponse response;
            try {
                response = brukerprofil.hentKontaktinformasjonOgPreferanser(request);
            } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet | HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
                throw new Exception("Person med id '" + brukerId + "': " + e.getMessage(), e);
            }
            XMLBruker bruker = (XMLBruker) response.getPerson();

            return on(bruker.getElektroniskKommunikasjonskanal())
                    .map(XML_ELEKTRONISK_ADRESSE)
                    .filter(isA(XMLEPost.class)).head()
                    .map(castTo(XMLEPost.class))
                    .map(IDENTIFIKATOR)
                    .getOrElse("");
        }

        @Override
        public Optional<String> hentEnhet() {
            // Mock frem til TPS er åpnet i SBS
            return optional("0219");

            // UTKOMMENTERT FREM TIL TPS ER ÅPNET I SBS
//            String fnr = SubjectHandler.getSubjectHandler().getUid();
//            try {
//                Bruker bruker = (Bruker) personV2.hentKjerneinformasjon(new HentKjerneinformasjonRequest().withIdent(fnr)).getPerson();
//                return optional(bruker.getHarAnsvarligEnhet().getEnhet().getOrganisasjonselementID());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        }

        private static final Transformer<XMLElektroniskKommunikasjonskanal, XMLElektroniskAdresse> XML_ELEKTRONISK_ADRESSE = new Transformer<XMLElektroniskKommunikasjonskanal, XMLElektroniskAdresse>() {
            @Override
            public XMLElektroniskAdresse transform(XMLElektroniskKommunikasjonskanal xmlElektroniskKommunikasjonskanal) {
                return xmlElektroniskKommunikasjonskanal.getElektroniskAdresse();
            }
        };

        private static final Transformer<XMLEPost, String> IDENTIFIKATOR = new Transformer<XMLEPost, String>() {
            @Override
            public String transform(XMLEPost xmlePost) {
                return xmlePost.getIdentifikator();
            }
        };

    }
}
