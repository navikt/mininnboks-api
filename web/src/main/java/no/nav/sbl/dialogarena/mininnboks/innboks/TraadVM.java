package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.innboks.utils.VisningUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.*;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class TraadVM implements Serializable {

    public final String id;
    public final List<Henvendelse> henvendelser;
    public final IModel<Boolean> lukket, besvareModus;
    public final IModel<String> ariaTekst;
    public final Temagruppe temagruppe;

    public TraadVM(String id, Temagruppe temagruppe, List<Henvendelse> henvendelser) {
        this.id = id;
        this.temagruppe = temagruppe;
        this.henvendelser = henvendelser;
        this.lukket = new CompoundPropertyModel<>(true);
        this.besvareModus = new CompoundPropertyModel<>(false);
        this.ariaTekst = Model.of(lagARIAHjelpeStreng(this));
    }

    public static String lagARIAHjelpeStreng(TraadVM traad) {
        Henvendelse nyesteHenvendelse = getNyesteHenvendelse(traad.henvendelser);
        String statusTekst = VisningUtils.henvendelseStatusTekst(nyesteHenvendelse);

        return String.format(
                "%d meldinger. Nyeste melding: %s %s",
                traad.henvendelser.size(),
                kortMedTid(nyesteHenvendelse.opprettet),
                statusTekst
        );
    }

    public void markerSomLest(HenvendelseService service) {
        for (Henvendelse henvendelse : henvendelser) {
            if (!henvendelse.erLest()) {
                henvendelse.markerSomLest();
                service.merkHenvendelseSomLest(henvendelse);
            }
        }
        this.ariaTekst.setObject(lagARIAHjelpeStreng(this));
    }

    public static Henvendelse getNyesteHenvendelse(List<Henvendelse> henvendelser) {
        return henvendelser.isEmpty() ? null : on(henvendelser).collect(NYESTE_OVERST).get(0);
    }

    public IModel<Henvendelse> nyesteHenvendelse() {
        return new AbstractReadOnlyModel<Henvendelse>() {
            @Override
            public Henvendelse getObject() {
                return henvendelser.isEmpty() ? null : on(henvendelser).collect(NYESTE_OVERST).get(0);
            }
        };
    }

    public IModel<List<Henvendelse>> tidligereHenvendelser() {
        return new AbstractReadOnlyModel<List<Henvendelse>>() {
            @Override
            public List<Henvendelse> getObject() {
                return henvendelser.isEmpty() ? henvendelser : on(henvendelser).collect(NYESTE_OVERST).subList(1, henvendelser.size());
            }
        };
    }

    public static IModel<Boolean> erLest(final List<Henvendelse> henvendelser) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return on(henvendelser).filter(where(ER_LEST, equalTo(false))).isEmpty();
            }
        };
    }

    public IModel<Boolean> kanBesvares() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return getNyesteHenvendelse(henvendelser).type == SPORSMAL_MODIA_UTGAAENDE;
            }
        };
    }

    public IModel<Integer> getTraadlengde() {
        return new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return henvendelser.size();
            }
        };
    }

    public IModel<Boolean> visTraadlengde() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelser.size() > 2;
            }
        };
    }

    public static List<TraadVM> tilTraader(List<Henvendelse> henvendelser) {
        List<Henvendelse> sortert = on(henvendelser).collect(NYESTE_OVERST);
        Map<String, List<Henvendelse>> traader = on(sortert).reduce(indexBy(TRAAD_ID), new LinkedHashMap<String, List<Henvendelse>>());
        return on(traader.values()).filter(ROTHENVENDELSEN_EKSISTERER).map(TIL_TRAAD_VM).collect();
    }

    private static final Transformer<List<Henvendelse>, TraadVM> TIL_TRAAD_VM = new Transformer<List<Henvendelse>, TraadVM>() {
        @Override
        public TraadVM transform(List<Henvendelse> henvendelser) {
            Henvendelse forsteHenvendelse = henvendelser.get(0);
            return new TraadVM(forsteHenvendelse.traadId, forsteHenvendelse.temagruppe, henvendelser);
        }
    };

    private static final Predicate<List<Henvendelse>> ROTHENVENDELSEN_EKSISTERER = new Predicate<List<Henvendelse>>() {
        @Override
        public boolean evaluate(List<Henvendelse> henvendelser) {
            return on(henvendelser).exists(ID_ER_LIK_TRAAD_ID);
        }
    };
    private static final Predicate<Henvendelse> ID_ER_LIK_TRAAD_ID = new Predicate<Henvendelse>() {
        @Override
        public boolean evaluate(Henvendelse henvendelse) {
            return henvendelse.id.equals(henvendelse.traadId);
        }
    };
}
