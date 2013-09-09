package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Henvendelse;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Henvendelsetype;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Henvendelsetype.SPORSMAL;

public class HenvendelseVM implements Serializable {

    public final Henvendelse henvendelse;

    public HenvendelseVM(Henvendelse henvendelse) {
        this.henvendelse = henvendelse;
    }

    public String getOpprettetDato() {
        return formatertDato(henvendelse.opprettet);
    }

    public String getLestDato() {
        String dato = formatertDato(henvendelse.lestDato);
        return dato != null ? "Sett " + dato : null;
    }

    public String getAvsender() {
        return avType(SPORSMAL).getObject() ? "Ola Nordmann" : "Fra: NAV";
    }

    private String formatertDato(DateTime dato) {
        return dato == null ? null :
                DateTimeFormat.forPattern("dd.MM.yyyy, HH:mm:ss")
                        .withLocale(Locale.getDefault())
                        .print(dato);
    }

    public IModel<Boolean> avType(final Henvendelsetype type) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelse.type == type;
            }
        };
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

}
