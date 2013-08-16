package no.nav.sbl.dialogarena.minehenvendelser.provider.rs;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.behandlingTransformer;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.soeknadTransformer;

@Path("/innsendinger")
public class InnsendingerProvider {

    @Inject
    BehandlingService behandlingService;

    @Inject
    SakogbehandlingService sakogbehandlingService;

    @Inject
    FoedselsnummerService foedselsnummerService;

    @Inject
    Kodeverk kodeverk;

    @Inject
    CmsContentRetriever innholdstekster;

    @GET
    @Path("/paabegynte")
    public List<Innsending> getPaabegynte() {

        List<Innsending> innsendinger = new ArrayList<>();
        for(Henvendelsesbehandling henvendelsesbehandling : behandlingService.hentPabegynteBehandlinger(foedselsnummerService.getFoedselsnummer())) {
            innsendinger.add(behandlingTransformer(innholdstekster, kodeverk).transform(henvendelsesbehandling));
        }
        return innsendinger;
    }

    @GET
    @Path("/mottatte")
    public List<Innsending> getMottatte() {

        List<Innsending> innsendinger = new ArrayList<>();
        for(Soeknad soeknad : sakogbehandlingService.finnMottatteSoeknader(foedselsnummerService.getFoedselsnummer())) {
            innsendinger.add(soeknadTransformer(innholdstekster).transform(soeknad));
        }
        return innsendinger;
    }

    @GET
    @Path("/underarbeid")
    public List<Innsending> getUnderArbeid() {

        List<Innsending> innsendinger = new ArrayList<>();
        for(Soeknad soeknad : sakogbehandlingService.finnSoeknaderUnderArbeid(foedselsnummerService.getFoedselsnummer())) {
            innsendinger.add(soeknadTransformer(innholdstekster).transform(soeknad));
        }
        return innsendinger;
    }

    @GET
    @Path("/ferdige")
    public List<Innsending> getFerdige() {

        List<Innsending> innsendinger = new ArrayList<>();
        for(Soeknad soeknad : sakogbehandlingService.finnFerdigeSoeknader(foedselsnummerService.getFoedselsnummer())) {
            innsendinger.add(soeknadTransformer(innholdstekster).transform(soeknad));
        }
        return innsendinger;
    }


}
