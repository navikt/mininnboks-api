package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.MOTTATT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.SoeknadsStatus.UNDER_ARBEID;

/**
 * Dette objektet representerer hva som logisk sett er en søknad for sluttbruker.
 * Fungerer som lokalt domeneobjekt mellom genererte klasser fra cxf og wicket.
 */
public final class Soeknad implements Serializable {

    public enum SoeknadsStatus {FERDIG, UNDER_ARBEID, MOTTATT }

    private DateTime paabegynt;
    private DateTime fullfoert;
    private DateTime mottatt;
    private String normertBehandlingsTid;
    private String tema;
    private String beskrivelse;

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
        if(mottatt == null) {
            throw new SystemException("illegal state", new RuntimeException());
        } else if(fullfoert != null) {
            return FERDIG;
        } else if(paabegynt != null) {
            return UNDER_ARBEID;
        }
        return MOTTATT;
    }

    public String getNormertBehandlingsTid() {
        return normertBehandlingsTid;
    }

    public DateTime getPaabegynt() {
        return paabegynt;
    }

    public DateTime getFullfoert() {
        return fullfoert;
    }

    public DateTime getMottatt() {
        return mottatt;
    }

    private static Transformer<Behandlingskjede, Soeknad> soeknadTransformer = new Transformer<Behandlingskjede, Soeknad>() {

        @Override
        public Soeknad transform(Behandlingskjede behandlingskjede) {
            Soeknad soeknad = new Soeknad();
            soeknad.tema = behandlingskjede.getBehandlingskjedetype().getValue();
            soeknad.beskrivelse = behandlingskjede.getBehandlingskjedetype().getKodeverksRef();
            soeknad.normertBehandlingsTid = getNormertTidString(behandlingskjede);
            soeknad.mottatt = new DateTime(behandlingskjede.getStart().getMillisecond());
            soeknad.paabegynt = optional(behandlingskjede.getStartNAVtid()).map(dateTimeTransformer()).getOrElse(null);
            soeknad.fullfoert = optional(behandlingskjede.getSluttNAVtid()).map(dateTimeTransformer()).getOrElse(null);
            return soeknad;
        }

    };

    private static Transformer<XMLGregorianCalendar, DateTime> dateTimeTransformer() {
        return new Transformer<XMLGregorianCalendar, DateTime>() {
            @Override
            public DateTime transform(XMLGregorianCalendar xmlGregorianCalendar) {
                return new DateTime(xmlGregorianCalendar.getMillisecond());
            }
        };
    }

    private static String getNormertTidString(Behandlingskjede behandlingskjede) {
        return behandlingskjede.getNormertBehandlingstid().getTid() + " " + behandlingskjede.getNormertBehandlingstid().getType().getValue();
    }
}
