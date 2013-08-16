package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;

import static no.nav.modig.lang.option.Optional.optional;

/**
 * Dette objektet representerer hva som logisk sett er en søknad for sluttbruker.
 * Fungerer som lokalt domeneobjekt mellom genererte klasser fra cxf og wicket.
 */
public final class Soeknad implements Serializable {

    public enum SoeknadsStatus {FERDIG, UNDER_ARBEID, MOTTATT }

    private DateTime underArbeid;
    private DateTime fullfoert;
    private DateTime start;
    private String normertBehandlingsTid;
    private String tema;
    private String beskrivelse;
    private SoeknadsStatus soeknadsStatus;
    private String behandlingsId;

    private Soeknad() {}

    public static Soeknad transformToSoeknad(Behandlingskjede behandlingskjede, SoeknadsStatus soeknadsStatus) {
        Soeknad soeknad = soeknadTransformer.transform(behandlingskjede);
        soeknad.soeknadsStatus = soeknadsStatus;
        return soeknad;
    }

    public String getBehandlingsId() {
        return behandlingsId;
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

    public DateTime getPaabegynt() {
        return underArbeid;
    }

    public DateTime getFullfoert() {
        return fullfoert;
    }

    public DateTime getStart() {
        return start;
    }

    private static Transformer<Behandlingskjede, Soeknad> soeknadTransformer = new Transformer<Behandlingskjede, Soeknad>() {

        @Override
        public Soeknad transform(Behandlingskjede behandlingskjede) {
            Soeknad soeknad = new Soeknad();
            soeknad.tema = behandlingskjede.getBehandlingskjedetype().getValue();
            soeknad.beskrivelse = behandlingskjede.getBehandlingskjedetype().getKodeverksRef();
            soeknad.normertBehandlingsTid = getNormertTidString(behandlingskjede);
            soeknad.start = new DateTime(behandlingskjede.getStart().getMillisecond());
            soeknad.underArbeid = optional(behandlingskjede.getStartNAVtid()).map(dateTimeTransformer()).getOrElse(null);
            soeknad.fullfoert = optional(behandlingskjede.getSluttNAVtid()).map(dateTimeTransformer()).getOrElse(null);
            soeknad.behandlingsId = behandlingskjede.getSisteBehandlingREF();
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
