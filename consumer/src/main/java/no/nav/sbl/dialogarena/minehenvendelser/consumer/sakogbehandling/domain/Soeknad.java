package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.UNDER_ARBEID;

/**
 * Dette objektet representerer hva som logisk sett er en søknad for sluttbruker.
 */
public final class Soeknad {

    public enum SoeknadsStatus { AVSLUTTET, UNDER_ARBEID }

    private DateTime startNAVTid;
    private DateTime sluttNAVTid;
    private String normertBehandlingsTid;
    private String tema;
    private String beskrivelse;
    private SoeknadsStatus soeknadsStatus;

    private Soeknad() {}

    public static Soeknad transformToSoeknad(Behandlingskjede behandlingskjede) {
        return soeknadTransformer.transform(behandlingskjede);
    }

    public String getTema() {
        return tema;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public SoeknadsStatus getSoeknadsStatus() {
        return soeknadsStatus;
    }

    public String getNormertBehandlingsTid() {
        return normertBehandlingsTid;
    }

    public DateTime getStartNAVTid() {
        return startNAVTid;
    }

    private static Transformer<Behandlingskjede, Soeknad> soeknadTransformer = new Transformer<Behandlingskjede, Soeknad>() {

        @Override
        public Soeknad transform(Behandlingskjede behandlingskjede) {
            Soeknad soeknad = new Soeknad();
            soeknad.tema = behandlingskjede.getBehandlingskjedetype().getValue();
            soeknad.beskrivelse = behandlingskjede.getBehandlingskjedetype().getKodeverksRef();
            soeknad.soeknadsStatus = evaluateStatus(behandlingskjede);
            soeknad.normertBehandlingsTid = getNormertTidString(behandlingskjede);
            soeknad.startNAVTid = new DateTime(behandlingskjede.getStartNAVtid().toGregorianCalendar().getTime());
            soeknad.sluttNAVTid = new DateTime(behandlingskjede.getSluttNAVtid().toGregorianCalendar().getTime());
            return soeknad;
        }

    };

    private static String getNormertTidString(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getNormertBehandlingstid().getTid() + behandlingskjede.getNormertBehandlingstid().getType().getValue();
    }

    private static SoeknadsStatus evaluateStatus(Behandlingskjede behandlingskjede) {
        if (behandlingskjede.getStartNAVtid() == null) {
            throw new SystemException("illegal state", new RuntimeException());
        } else if (behandlingskjede.getSluttNAVtid() != null) {
            return AVSLUTTET;
        }
        return UNDER_ARBEID;                                       }

}
