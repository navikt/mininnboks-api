package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class InnboksModell extends CompoundPropertyModel<InnboksVM> {

    public InnboksModell(InnboksVM innboks) {
        super(innboks);
    }

    public InnboksVM getInnboksVM() {
        return getObject();
    }

    public IModel<Boolean> ingenMeldinger() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return InnboksModell.this.getObject().getMeldinger().size() == 0;
            }
        };
    }

    public final IModel<Boolean> erValgtMelding(final MeldingVM melding) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<MeldingVM> valgtMelding = getInnboksVM().getValgtMelding();
                return valgtMelding.isSome() && valgtMelding.get() == melding;
            }
        };
    }

    public IModel<Boolean> ingenMeldingValgt() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<MeldingVM> valgtMelding = getInnboksVM().getValgtMelding();
                return !valgtMelding.isSome();
            }
        };
    }

    public IModel<Boolean> valgtMeldingAvType(final Meldingstype type) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<MeldingVM> valgtMelding = getInnboksVM().getValgtMelding();
                return valgtMelding.isSome() && valgtMelding.get().melding.type == type;
            }
        };
    }

    public boolean skjulAlleMeldinger = false;
    public IModel<Boolean> alleMeldingerSkalSkjules() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return skjulAlleMeldinger;
            }
        };
    }
}
