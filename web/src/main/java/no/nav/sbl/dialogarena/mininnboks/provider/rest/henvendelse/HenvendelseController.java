package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad;
import org.apache.commons.collections15.Transformer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.TRAAD_ID;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad.NYESTE_FORST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/henvendelse")
public class HenvendelseController {


    @Inject
    private HenvendelseService henvendelseService;

    @RequestMapping(value = "traader", method = GET)
    public List<Traad> traader() {
        String fnr = getSubjectHandler().getUid();
        List<Henvendelse> henvendelser = henvendelseService.hentAlleHenvendelser(fnr);
        final Map<String, List<Henvendelse>> traader = on(henvendelser).reduce(indexBy(TRAAD_ID));

        return on(traader.values()).map(new Transformer<List<Henvendelse>, Traad>() {
            @Override
            public Traad transform(List<Henvendelse> henvendelser) {
                return new Traad(henvendelser);
            }
        }).collect(NYESTE_FORST);
    }

    @RequestMapping(value = "traad/{id}", method = GET)
    public Traad hentTraad(@PathVariable String id) {
        return new Traad(henvendelseService.hentTraad(id));
    }

    private static <T> Transformer<List<T>, T> max(final Comparator<? super T> comparator) {
        return min(reverseOrder(comparator));
    }

    private static <T> Transformer<List<T>, T> min(final Comparator<? super T> comparator) {
        return new Transformer<List<T>, T>() {
            @Override
            public T transform(List<T> list) {
                return on(list).collect(comparator).get(0);
            }
        };
    }
}
