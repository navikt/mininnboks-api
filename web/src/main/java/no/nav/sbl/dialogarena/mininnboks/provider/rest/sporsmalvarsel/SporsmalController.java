package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/sporsmal")
public class SporsmalController {

    @Inject
    private HenvendelseService henvendelseService;

    @RequestMapping(value = "/ubehandlet", method = GET)
    public List<SporsmalVarsel> ulesteSporsmal() {
        return SporsmalVarselUtils.hentUbehandledeSporsmal(henvendelseService.hentAlleHenvendelser(getSubjectHandler().getUid()));
    }
}
