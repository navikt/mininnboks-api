package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Map.Entry;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.Henvendelse.NYESTE_OVERST;

public class TraadVM implements Serializable {

    public final String id;
    public final List<Henvendelse> henvendelser;

    public IModel<Boolean> lukket = new CompoundPropertyModel<>(true);

    public TraadVM(String id, List<Henvendelse> henvendelser) {
        this.id = id;
        this.henvendelser = henvendelser;
    }

    public void markerSomLest(HenvendelseService service) {
        for (Henvendelse henvendelse : henvendelser) {
            henvendelse.markerSomLest();
            service.merkHenvendelseSomLest(henvendelse.id);
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
        Map<String, List<Henvendelse>> traader = new LinkedHashMap<>();

        for (Henvendelse henvendelse : on(henvendelser).collect(NYESTE_OVERST)) {

            String traadId = henvendelse.traadId;

            if (traader.containsKey(traadId)) {
                List<Henvendelse> traad = new ArrayList<>(traader.get(traadId));
                traad.add(henvendelse);
                traader.put(traadId, traad);
            } else {
                traader.put(traadId, asList(henvendelse));
            }
        }

        return on(traader.entrySet()).map(TIL_TRAAD_VM).collect();
    }

    public static final Transformer<Entry<String, List<Henvendelse>>, TraadVM> TIL_TRAAD_VM = new Transformer<Entry<String, List<Henvendelse>>, TraadVM>() {
        @Override
        public TraadVM transform(Entry<String, List<Henvendelse>> traad) {
            return new TraadVM(traad.getKey(), traad.getValue());
        }
    };
}
