package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.kontaktdetaljer;

import no.nav.sbl.dialogarena.types.Copyable;

import java.io.Serializable;

public class Preferanser implements Serializable, Copyable<Preferanser> {

    private boolean elektroniskSamtykke;
    private Maalform maalform = new Maalform();

    public boolean isElektroniskSamtykke() {
        return elektroniskSamtykke;
    }

    public void setElektroniskSamtykke(boolean elektroniskSamtykke) {
        this.elektroniskSamtykke = elektroniskSamtykke;
    }

    public Maalform getMaalform() {
        return maalform;
    }

    @Override
    public Preferanser copy() {
        Preferanser preferanser = new Preferanser();
        preferanser.setMaalform(this.getMaalform().copy());
        preferanser.setElektroniskSamtykke(this.elektroniskSamtykke);
        return preferanser;
    }

    public void setMaalform(Maalform maalform) {
        this.maalform = maalform;
    }

    public class Maalform implements Serializable, Copyable<Maalform>{
        private String kodeverkRef;

        public String getKodeverkRef() {
            return kodeverkRef;
        }

        public void setKodeverkRef(String kodeverkRef) {
            this.kodeverkRef = kodeverkRef;
        }

        @Override
        public Maalform copy() {
            Maalform maalform = new Maalform();
            maalform.setKodeverkRef(this.kodeverkRef);
            return maalform;
        }
    }
}