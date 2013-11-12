package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.HenvendelseVM.TRAAD_ID;

public class InnboksModell extends CompoundPropertyModel<InnboksVM> {

    public InnboksModell(InnboksVM innboks) {
        super(innboks);
    }

    public InnboksVM getInnboksVM() {
        return getObject();
    }

    public IModel<Boolean> ingenHenvendelser() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return InnboksModell.this.getObject().getHenvendelser().size() == 0;
            }
        };
    }

    public final IModel<Boolean> erValgtHenvendelse(final HenvendelseVM henvendelse) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getInnboksVM().getValgtHenvendelse();
                return valgtHenvendelse.isSome() && valgtHenvendelse.get() == henvendelse;
            }
        };
    }

    public IModel<Boolean> ingenHenvendelseValgt() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getInnboksVM().getValgtHenvendelse();
                return !valgtHenvendelse.isSome();
            }
        };
    }

    public IModel<Boolean> valgtHenvendelseAvType(final Henvendelsetype type) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getInnboksVM().getValgtHenvendelse();
                return valgtHenvendelse.isSome() && valgtHenvendelse.get().avType(type);
            }
        };
    }

    public IModel<Integer> getTraadLengde(final String traadID) {
        return new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return on(InnboksModell.this.getInnboksVM().getHenvendelser()).filter(where(TRAAD_ID, equalTo(traadID))).collect().size();
            }
        };
    }

    public CompoundPropertyModel<Boolean> alleHenvendelserSkalSkjulesHvisLitenSkjerm = new CompoundPropertyModel<>(false);
}
