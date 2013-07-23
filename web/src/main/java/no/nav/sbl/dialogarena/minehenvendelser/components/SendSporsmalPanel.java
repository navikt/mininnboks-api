package no.nav.sbl.dialogarena.minehenvendelser.components;

import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;

public class SendSporsmalPanel extends Panel {

    @Inject
    private SporsmalOgSvarPortType sporsmalOgSvarService;

    String fodselsnr;
    NesteSide nesteSide;

    public SendSporsmalPanel(String id, SporsmalOgSvarModel model, String fodselsnr, final NesteSide nesteSide) {
        super(id);
        this.fodselsnr = fodselsnr;
        this.nesteSide = nesteSide;
        add(new SporsmalForm("sporsmal-form", model));
    }

    private final class SporsmalForm extends Form<SporsmalOgSvarVM> {

        private SporsmalForm(String id, SporsmalOgSvarModel model) {
            super(id, model);
            TextArea fritekst = new TextArea("fritekst");
            Label tema = new Label("tema");
            add(fritekst, tema);
        }

        @Override
        protected void onSubmit() {
            SporsmalOgSvarVM spsm = getModelObject();
            sporsmalOgSvarService.opprettSporsmal(new WSSporsmal().withFritekst(spsm.fritekst).withTema(spsm.tema), fodselsnr);
            nesteSide.neste();
        }
    }

}
