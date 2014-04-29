package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.send;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Stegnavigator;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.Tema;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class SkrivPage extends BasePage {

    @Inject
    private HenvendelseService service;
    public SkrivPage(PageParameters parameters) {
        Sporsmal sporsmal = new Sporsmal();
        sporsmal.setTema(Tema.valueOf(parameters.get("tema").toString()));
        add(new SendPanel("send", new CompoundPropertyModel<>(sporsmal), new Stegnavigator() {
            @Override
            public void neste() {

            }
        }, service));
    }
}
