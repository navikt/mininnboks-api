package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Melding;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.MeldingVM.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.MeldingVM.TIL_MELDING_VM;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.MeldingVM.TRAAD_ID;

public class InnboksVM implements Serializable {

    private List<MeldingVM> meldinger;

    private Optional<MeldingVM> valgtMelding = none();

    public InnboksVM(List<Melding> nyeMeldinger) {
        oppdaterMeldingerFra(nyeMeldinger);
    }

    public List<MeldingVM> getMeldinger() {
        return meldinger;
    }

    public List<MeldingVM> getNyesteHenvendelseITraad() {
        List<String> traadIder = new ArrayList<>();
        for (String id : on(getMeldinger()).map(TRAAD_ID)) {
            if (!traadIder.contains(id)) {
                traadIder.add(id);
            }
        }
        List<MeldingVM> nyesteHenvendelser = new ArrayList<>();
        for (String id : traadIder) {
            List<MeldingVM> henvendelserITraad = on(getMeldinger()).filter(where(TRAAD_ID, equalTo(id))).collect(NYESTE_OVERST);
            nyesteHenvendelser.add(henvendelserITraad.get(0));
        }
        return on(nyesteHenvendelser).collect(NYESTE_OVERST);
    }

    public List<MeldingVM> getTraad() {
        for (MeldingVM meldingVM : valgtMelding) {
            return on(meldinger).filter(where(TRAAD_ID, equalTo(meldingVM.melding.traadId))).collect(NYESTE_OVERST);
        }
        return emptyList();
    }

    public List<MeldingVM> getTidligereHenvendelser() {
        List<MeldingVM> traad = getTraad();
        return traad.isEmpty() ? traad : traad.subList(1, traad.size());
    }

    public MeldingVM getNyesteHenvendelse() {
        List<MeldingVM> traad = getTraad();
        return traad.isEmpty() ? null : traad.get(0);
    }

    public final void oppdaterMeldingerFra(List<Melding> meldinger) {
        this.meldinger = on(meldinger).map(TIL_MELDING_VM).collect(NYESTE_OVERST);
    }

    public Optional<MeldingVM> getValgtMelding() {
        return valgtMelding;
    }

    public void setValgtMelding(MeldingVM valgtMelding) {
        this.valgtMelding = optional(valgtMelding);
    }

}
