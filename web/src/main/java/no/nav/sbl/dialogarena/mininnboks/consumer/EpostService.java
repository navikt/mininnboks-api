package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLEPost;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLElektroniskAdresse;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLElektroniskKommunikasjonskanal;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;
import org.apache.commons.collections15.Transformer;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.isA;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;

public interface EpostService {

    public String hentEpostadresse() throws Exception;

    public class Default implements EpostService {

        private final BrukerprofilPortType brukerprofil;

        public Default(BrukerprofilPortType brukerprofilPortType) {
            this.brukerprofil = brukerprofilPortType;
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
