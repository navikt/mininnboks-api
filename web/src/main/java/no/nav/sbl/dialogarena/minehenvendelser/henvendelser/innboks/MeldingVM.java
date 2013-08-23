package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Melding;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.format.DateTimeFormat;

public class MeldingVM implements Serializable {
	
    public final Melding melding;

    public MeldingVM(Melding melding) {
        this.melding = melding;
    }

    public String getOpprettetDato() {
        return DateTimeFormat.forPattern("dd.MM.yyyy, HH:mm:ss")
                .withLocale(Locale.getDefault())
                .print(melding.opprettet);
    }

    public IModel<Boolean> erLest() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return melding.erLest();
            }
        };
    }

    public static final Transformer<Melding, MeldingVM> TIL_MELDING_VM = new Transformer<Melding, MeldingVM>() {
        @Override
        public MeldingVM transform(Melding melding) {
            return new MeldingVM(melding);
        }
    };

    public static final Transformer<MeldingVM, String> TRAAD_ID = new Transformer<MeldingVM, String>() {
        @Override
        public String transform(MeldingVM meldingVM) {
            return meldingVM.melding.traadId;
        }
    };

    public static final Comparator<MeldingVM> NYESTE_NEDERST = new Comparator<MeldingVM>() {
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o1.melding.opprettet.compareTo(o2.melding.opprettet);
        }
    };
}
