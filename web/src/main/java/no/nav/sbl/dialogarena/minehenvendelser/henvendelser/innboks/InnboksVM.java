package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Melding;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.MeldingVM.TIL_MELDING_VM;

public class InnboksVM implements Serializable {

    private List<MeldingVM> meldinger;

    private Optional<MeldingVM> valgtMelding = none();

    public InnboksVM(List<Melding> nyeMeldinger) {
        oppdaterMeldingerFra(nyeMeldinger);
    }

    public List<MeldingVM> getMeldinger() {
        return meldinger;
    }

    public List<MeldingVM> getTraad() {
        for (MeldingVM meldingVM : valgtMelding) {
            return on(meldinger).filter(where(MeldingVM.TRAAD_ID, equalTo(meldingVM.melding.traadId))).collect(MeldingVM.NYESTE_NEDERST);
        }
        return emptyList();
    }

    public final void oppdaterMeldingerFra(List<Melding> meldinger) {
        this.meldinger = on(meldinger).map(TIL_MELDING_VM).collect(nyesteOverst);
    }

    public Optional<MeldingVM> getValgtMelding() {
        return valgtMelding;
    }

    public void setValgtMelding(MeldingVM valgtMelding) {
        this.valgtMelding = optional(valgtMelding);
    }
    private static Comparator<MeldingVM> nyesteOverst = new Comparator<MeldingVM>() {
        public int compare(MeldingVM m1, MeldingVM m2) {
            return m2.melding.opprettet.compareTo(m1.melding.opprettet);
        }
    };
}
