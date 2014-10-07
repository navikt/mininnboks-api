package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.innboks.utils.VisningUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.ER_LEST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.TRAAD_ID;
import static no.nav.sbl.dialogarena.mininnboks.innboks.utils.VisningUtils.henvendelseStatusTekst;
import static no.nav.sbl.dialogarena.time.Datoformat.kortMedTid;

public class TraadVM implements Serializable {

    public final String id;
    public final List<Henvendelse> henvendelser;
    public final IModel<Boolean> lukket;
    public final IModel<String> statusTekst;
    public final IModel<String> ariaTekst;

    public TraadVM(String id, List<Henvendelse> henvendelser) {
        this.id = id;
        this.henvendelser = henvendelser;
        this.lukket = new CompoundPropertyModel<>(true);
        this.statusTekst = Model.of(henvendelseStatusTekst(getNyesteHenvendelse(henvendelser)));
        this.ariaTekst = Model.of(lagARIAHjelpeStreng(this));
    }

    public static String lagARIAHjelpeStreng(TraadVM traad) {
        Henvendelse nyesteHenvendelse = getNyesteHenvendelse(traad.henvendelser);
        String statusTekst = VisningUtils.henvendelseStatusTekst(nyesteHenvendelse);

        return String.format(
                "%d meldinger.\nStatus: %s\nNyeste Melding: %s",
                traad.henvendelser.size(),
                statusTekst,
                kortMedTid(nyesteHenvendelse.opprettet)
        );
    }

    public void markerSomLest(HenvendelseService service) {
        for (Henvendelse henvendelse : henvendelser) {
            if (!henvendelse.erLest()) {
                henvendelse.markerSomLest();
                service.merkHenvendelseSomLest(henvendelse);
            }
        }
        oppdater();
    }

    public void oppdater() {
        this.statusTekst.setObject(henvendelseStatusTekst(getNyesteHenvendelse(henvendelser)));
        this.ariaTekst.setObject(lagARIAHjelpeStreng(this));
    }

    public static Henvendelse getNyesteHenvendelse(List<Henvendelse> henvendelser) {
        return henvendelser.isEmpty() ? null : on(henvendelser).collect(NYESTE_OVERST).get(0);
    }

    public static List<Henvendelse> getTidligereHenvendelser(List<Henvendelse> henvendelser) {
        return henvendelser.isEmpty() ? henvendelser : on(henvendelser).collect(NYESTE_OVERST).subList(1, henvendelser.size());
    }

    public static IModel<Boolean> erLest(final List<Henvendelse> henvendelser) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return on(henvendelser).filter(where(ER_LEST, equalTo(false))).isEmpty();
            }
        };
    }

    public static List<TraadVM> tilTraader(List<Henvendelse> henvendelser) {
        List<Henvendelse> sortert = sortertPaaOpprettetDato(henvendelser);
        Map<String, List<Henvendelse>> traader = on(sortert).reduce(indexBy(TRAAD_ID), new LinkedHashMap<String, List<Henvendelse>>());
        return on(traader.values()).filter(ROTHENVENDELSEN_EKSISTERER).map(TIL_TRAAD_VM).collect();
    }

    private static List<Henvendelse> sortertPaaOpprettetDato(List<Henvendelse> henvendelser) {
        List<Henvendelse> sortert = new ArrayList<>(henvendelser);
        sort(sortert, NYESTE_OVERST);
        return sortert;
    }

    private static final Transformer<List<Henvendelse>, TraadVM> TIL_TRAAD_VM = new Transformer<List<Henvendelse>, TraadVM>() {
        @Override
        public TraadVM transform(List<Henvendelse> henvendelser) {
            return new TraadVM(henvendelser.get(0).traadId, henvendelser);
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
