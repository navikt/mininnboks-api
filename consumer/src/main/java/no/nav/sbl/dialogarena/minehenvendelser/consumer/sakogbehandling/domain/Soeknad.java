package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.math.BigInteger;

import static no.nav.modig.lang.option.Optional.optional;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.UNDER_ARBEID;

/**
 * Dette objektet representerer hva som logisk sett er en søknad for sluttbruker.
 */
public final class Soeknad {

    public enum SoeknadsStatus { AVSLUTTET, UNDER_ARBEID;}

    private DateTime startNAVTid;
    private BigInteger normalBehandlingstid;
    private String header;
    private String beskrivelse;
    private SoeknadsStatus soeknadsStatus;

    private Soeknad() {}

    public static Soeknad transformToSoeknad(Behandlingskjede behandlingskjede) {
        return ipSakTransformer.transform(behandlingskjede);
    }

    public String getHeader() {
        return header;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public SoeknadsStatus getSoeknadsStatus() {
        return soeknadsStatus;
    }

    public BigInteger getNormalBehandlingstid() {
        return normalBehandlingstid;
    }

    public DateTime getStartNAVTid() {
        return startNAVTid;
    }

    private static Transformer<Behandlingskjede, Soeknad> ipSakTransformer = new Transformer<Behandlingskjede, Soeknad>() {

        @Override
        public Soeknad transform(Behandlingskjede behandlingskjede) {
            Soeknad soeknad = new Soeknad();
            soeknad.beskrivelse = optional(behandlingskjede.getBehandlingskjedetype().getKodeRef()).getOrElse("");
            soeknad.header = optional(behandlingskjede.getBehandlingskjedeId()).getOrElse("");
            soeknad.soeknadsStatus = optional(evaluateStatus(behandlingskjede)).getOrElse(null);
            soeknad.normalBehandlingstid = optional(behandlingskjede.getNormertBehandlingstid().getTid()).getOrElse(null);
            soeknad.startNAVTid = new DateTime(behandlingskjede.getStartNAVtid().toGregorianCalendar().getTime());
            return soeknad;
        }

    };

    private static SoeknadsStatus evaluateStatus(Behandlingskjede behandlingskjede) {
        if (behandlingskjede.getStartNAVtid() == null) {
            throw new SystemException("illegal state", new RuntimeException());
        } else if (behandlingskjede.getSluttNAVtid() != null) {
            return AVSLUTTET;
        }
        return UNDER_ARBEID;                                       }

}
