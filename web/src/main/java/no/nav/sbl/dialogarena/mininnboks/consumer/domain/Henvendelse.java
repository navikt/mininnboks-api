package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import org.apache.commons.collections15.Transformer;
import org.apache.wicket.protocol.http.WebApplication;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.FRA_NAV;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.KassertInnholdUtils.henvendelseTemagruppeKey;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class Henvendelse implements Serializable {

    public String id;
    public String traadId, fritekst, kanal, eksternAktor, tilknyttetEnhet, temagruppeNavn, statusTekst;
    public Henvendelsetype type;
    public Temagruppe temagruppe;
    public DateTime opprettet, avsluttet;
    public Boolean kassert = false;
    private DateTime lestDato;

    public Henvendelse(String id) {
        this.id = id;
    }

    public Henvendelse(String fritekst, Temagruppe temagruppe) {
        this.fritekst = fritekst;
        this.temagruppe = temagruppe;
    }

    public void markerSomLest(DateTime lestDato) {
        this.lestDato = lestDato;
    }

    public void markerSomLest() {
        this.lestDato = DateTime.now();
    }

    public DateTime getLestDato() {
        return lestDato;
    }

    public boolean isLest() {
        return lestDato != null;
    }

    public String getAvsenderBildeUrl() {
        String imgUrl = WebApplication.get().getServletContext().getContextPath() + "/img/";
        if (FRA_NAV.contains(type)) {
            return imgUrl + "nav-logo.svg";
        } else {
            return imgUrl + "siluett.svg";
        }
    }

    public String getAvsenderBildeAltKey() {
        if (FRA_NAV.contains(type)) {
            return "innboks.avsender.nav";
        } else {
            return "innboks.avsender.bruker";
        }
    }

    public String getSendtDato() {
        return kortMedTid(opprettet);
    }

    public String getTemagruppeKey() {
        return henvendelseTemagruppeKey(temagruppe);
    }

    public static final Transformer<Henvendelse, DateTime> OPPRETTET = new Transformer<Henvendelse, DateTime>() {
        @Override
        public DateTime transform(Henvendelse henvendelse) {
            return henvendelse.opprettet;
        }
    };

    public static final Transformer<Henvendelse, String> ID = new Transformer<Henvendelse, String>() {
        @Override
        public String transform(Henvendelse henvendelse) {
            return henvendelse.id;
        }
    };

    public static final Transformer<Henvendelse, String> TRAAD_ID = new Transformer<Henvendelse, String>() {
        @Override
        public String transform(Henvendelse henvendelse) {
            return henvendelse.traadId;
        }
    };

    public static final Transformer<Henvendelse, Boolean> ER_LEST = new Transformer<Henvendelse, Boolean>() {
        @Override
        public Boolean transform(Henvendelse henvendelse) {
            return henvendelse.isLest();
        }
    };

    public static final Comparator<Henvendelse> NYESTE_OVERST = reverseOrder(compareWith(OPPRETTET));
}
