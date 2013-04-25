package no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.*;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus.FERDIG;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus.UNDER_ARBEID;

public class BehandlingServiceDummy implements BehandlingService {

    @Override
    public List<Behandling> hentBehandlinger(String aktoerId) {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(createDummyBehandling(FERDIG, "hovedSkjemaId1" , new String[]{"id2", "id3"}, new WSInnsendingsValg[]{WSInnsendingsValg.LASTET_OPP, WSInnsendingsValg.SEND_SENERE}));
        behandlinger.add(createDummyBehandling(UNDER_ARBEID, "hovedSkjemaId2", new String[]{"id4", "id5"}, new WSInnsendingsValg[]{WSInnsendingsValg.SENDES_IKKE, WSInnsendingsValg.SEND_SENERE}));
        behandlinger.add(createDummyBehandling(UNDER_ARBEID, "hovedSkjemaId2", new String[]{"id4", "id5", "id6"}, new WSInnsendingsValg[]{WSInnsendingsValg.SENDES_IKKE, WSInnsendingsValg.SEND_SENERE, WSInnsendingsValg.LASTET_OPP}));
        return behandlinger;
    }

    private Behandling createDummyBehandling(WSBehandlingsstatus status,String hovedskjemaKodeverkId, String[] dokumenterKodeverk, WSInnsendingsValg[] valg) {
        WSBrukerBehandling wsBrukerBehandling = createWsBehandlingMock();
        wsBrukerBehandling.withStatus(status);
        WSDokumentForventningOppsummeringer wsDokumentForventningOppsummeringer = new WSDokumentForventningOppsummeringer();
        wsBrukerBehandling.withDokumentForventningOppsummeringer(wsDokumentForventningOppsummeringer);
        wsDokumentForventningOppsummeringer.getDokumentForventningOppsummering().add(
                new WSDokumentForventningOppsummering().withKodeverkId(hovedskjemaKodeverkId).withHovedskjema(true).withInnsendingsValg(WSInnsendingsValg.LASTET_OPP)
        );
        for (int i = 0; i < dokumenterKodeverk.length; i++) {
            wsDokumentForventningOppsummeringer.getDokumentForventningOppsummering().add(
                    new WSDokumentForventningOppsummering().withInnsendingsValg(valg[i])
                    .withKodeverkId(dokumenterKodeverk[i]));
        }
        return transformToBehandling(wsBrukerBehandling);
    }


}
