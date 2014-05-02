package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.Session;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.Comparator;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype.SVAR;

public class HenvendelseVM implements Serializable {

    public final Henvendelse henvendelse;

    public HenvendelseVM(Henvendelse henvendelse) {
        this.henvendelse = henvendelse;
    }

    public String getLangOpprettetDato() {
        return Datoformat.langMedTid(henvendelse.opprettet);
    }

    public String getKortOpprettetDato() {
        return Datoformat.kortMedTid(henvendelse.opprettet);
    }

    public String getLestDato() {
        return avType(SVAR) ? "Lest: " + Datoformat.kortMedTid(henvendelse.lestDato) : null;
    }

    public boolean avType(final Henvendelsetype type) {
        return henvendelse.type == type;
    }

    public IModel<Boolean> erLest() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelse.erLest();
            }
        };
    }

    public static final Transformer<Henvendelse, HenvendelseVM> TIL_HENVENDELSE_VM = new Transformer<Henvendelse, HenvendelseVM>() {
        @Override
        public HenvendelseVM transform(Henvendelse henvendelse) {
            return new HenvendelseVM(henvendelse);
        }
    };

    public static final Transformer<HenvendelseVM, String> TRAAD_ID = new Transformer<HenvendelseVM, String>() {
        @Override
        public String transform(HenvendelseVM henvendelseVM) {
            return henvendelseVM.henvendelse.traadId;
        }
    };

    public static final Comparator<HenvendelseVM> NYESTE_OVERST = new Comparator<HenvendelseVM>() {
        public int compare(HenvendelseVM m1, HenvendelseVM m2) {
            return m2.henvendelse.opprettet.compareTo(m1.henvendelse.opprettet);
        }
    };

    private String formatertDato(final DateTime dato, final String format) {
        return dato == null ? null :
                DateTimeFormat.forPattern(format).withLocale(Session.get().getLocale()).print(dato);
    }

}
