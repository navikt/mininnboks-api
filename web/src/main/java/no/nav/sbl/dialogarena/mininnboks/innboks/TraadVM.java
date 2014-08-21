package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse.TRAAD_ID;

public class TraadVM implements Serializable {

    public final String id;
    public final List<Henvendelse> henvendelser;
    public final IModel<Boolean> lukket;

    public TraadVM(String id, List<Henvendelse> henvendelser) {
        this.id = id;
        this.henvendelser = henvendelser;
        this.lukket = new CompoundPropertyModel<>(true);
    }

    public void markerSomLest(HenvendelseService service) {
        for (Henvendelse henvendelse : henvendelser) {
            if (!henvendelse.erLest()) {
                henvendelse.markerSomLest();
                service.merkHenvendelseSomLest(henvendelse);
            }
        }
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
                for (Henvendelse henvendelse : henvendelser) {
                    if (!henvendelse.erLest()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static List<TraadVM> tilTraader(List<Henvendelse> henvendelser) {
        sorterHenvendelserPaaOpprettetdato(henvendelser);
        Map<String, List<Henvendelse>> traader = on(henvendelser).reduce(indexBy(TRAAD_ID), new LinkedHashMap<String, List<Henvendelse>>());
        for (List<Henvendelse> henvendelseList : traader.values()) {
            sorterHenvendelserPaaOpprettetdato(henvendelseList);
        }
        return on(traader.values()).map(TIL_TRAAD_VM).collect();
    }

    private static void sorterHenvendelserPaaOpprettetdato(List<Henvendelse> henvendelser) {
        sort(henvendelser, NYESTE_OVERST);
    }

    private static final Transformer<List<Henvendelse>, TraadVM> TIL_TRAAD_VM = new Transformer<List<Henvendelse>, TraadVM>() {
        @Override
        public TraadVM transform(List<Henvendelse> henvendelser) {
            return new TraadVM(henvendelser.get(0).traadId, henvendelser);
        }
    };

}
