package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelsetype;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.HenvendelseVM.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.HenvendelseVM.TIL_HENVENDELSE_VM;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.HenvendelseVM.TRAAD_ID;
import static no.nav.sbl.dialogarena.minehenvendelser.innboks.TraadVM.tilTraader;

public class InnboksVM implements Serializable {

    private List<HenvendelseVM> henvendelser;

    private List<TraadVM> traader;

    private Optional<HenvendelseVM> valgtHenvendelse = none();

    public InnboksVM(List<Henvendelse> nyeHenvendelser) {
        oppdaterHenvendelserFra(nyeHenvendelser);

        traader = tilTraader(nyeHenvendelser);
    }

    public List<HenvendelseVM> getHenvendelser() {
        return henvendelser;
    }

    public List<TraadVM> getTraader() {
        return traader;
    }

    public List<HenvendelseVM> getNyesteHenvendelseITraad() {
        List<HenvendelseVM> nyesteHenvendelser = new ArrayList<>();
        for (String id : alleTraadIder()) {
            List<HenvendelseVM> henvendelserITraad = on(getHenvendelser()).filter(where(TRAAD_ID, equalTo(id))).collect(NYESTE_OVERST);
            nyesteHenvendelser.add(henvendelserITraad.get(0));
        }
        return on(nyesteHenvendelser).collect(NYESTE_OVERST);
    }

    private List<String> alleTraadIder() {
        List<String> traadIder = new ArrayList<>();
        for (String id : on(getHenvendelser()).map(TRAAD_ID)) {
            if (!traadIder.contains(id)) {
                traadIder.add(id);
            }
        }
        return traadIder;
    }

    public List<HenvendelseVM> getTraad() {
        if (valgtHenvendelse.isSome()) {
            return on(henvendelser).filter(where(TRAAD_ID, equalTo(valgtHenvendelse.get().henvendelse.traadId))).collect(NYESTE_OVERST);
        }
        return emptyList();
    }

    public List<HenvendelseVM> getTidligereHenvendelser() {
        List<HenvendelseVM> traad = getTraad();
        return traad.isEmpty() ? traad : traad.subList(1, traad.size());
    }

    public HenvendelseVM getNyesteHenvendelse() {
        List<HenvendelseVM> traad = getTraad();
        return traad.isEmpty() ? null : traad.get(0);
    }

    public final void oppdaterHenvendelserFra(List<Henvendelse> henvendelser) {
        this.henvendelser = on(henvendelser).map(TIL_HENVENDELSE_VM).collect(NYESTE_OVERST);
    }

    public Optional<HenvendelseVM> getValgtHenvendelse() {
        return valgtHenvendelse;
    }

    public void setValgtHenvendelse(HenvendelseVM valgtHenvendelse) {
        this.valgtHenvendelse = optional(valgtHenvendelse);
    }

    public IModel<Boolean> ingenHenvendelser() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return getHenvendelser().size() == 0;
            }
        };
    }

    public final IModel<Boolean> erValgtHenvendelse(final HenvendelseVM henvendelse) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getValgtHenvendelse();
                return valgtHenvendelse.isSome() && valgtHenvendelse.get() == henvendelse;
            }
        };
    }

    public IModel<Boolean> valgtHenvendelseAvType(final Henvendelsetype type) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getValgtHenvendelse();
                return valgtHenvendelse.isSome() && valgtHenvendelse.get().avType(type);
            }
        };
    }

    public IModel<Integer> getTraadLengde(final String traadID) {
        return new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return on(getHenvendelser()).filter(where(TRAAD_ID, equalTo(traadID))).collect().size();
            }
        };
    }

    public CompoundPropertyModel<Boolean> alleHenvendelserSkalSkjulesHvisLitenSkjerm = new CompoundPropertyModel<>(false);
}
