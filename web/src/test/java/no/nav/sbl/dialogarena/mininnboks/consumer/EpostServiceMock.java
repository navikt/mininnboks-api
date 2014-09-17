package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;

public class EpostServiceMock implements EpostService {

    @Override
    public String hentEpostadresse() throws Exception {
        String brukerId = SubjectHandler.getSubjectHandler().getUid();
        if (brukerId.startsWith("2")) {
            return "epostadresse@example.com";
        } else if (brukerId.startsWith("9")) {
            Exception e = new HentKontaktinformasjonOgPreferanserPersonIkkeFunnet("Dette er message fra HentKontaktinformasjonOgPreferanserPersonIkkeFunnet");
            throw new Exception("Person med id '" + brukerId + "': " + e.getMessage(), e);
        } else {
            return "";
        }
    }

}