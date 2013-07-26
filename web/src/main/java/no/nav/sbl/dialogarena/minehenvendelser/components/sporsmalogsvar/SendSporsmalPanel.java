package no.nav.sbl.dialogarena.minehenvendelser.components.sporsmalogsvar;

import javax.inject.Inject;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class SendSporsmalPanel extends Panel {

    @Inject
    private SporsmalOgSvarPortType sporsmalOgSvarService;

    private String fodselsnr;
    private SideNavigerer sideNavigerer;

    public SendSporsmalPanel(String id, CompoundPropertyModel<Sporsmal> model, String fodselsnr, final SideNavigerer sideNavigerer) {
        super(id);
        this.fodselsnr = fodselsnr;
        this.sideNavigerer = sideNavigerer;
        add(new SporsmalForm("sporsmal-form", model));
    }

    private final class SporsmalForm extends Form<Sporsmal> {

        private SporsmalForm(String id, CompoundPropertyModel<Sporsmal> model) {
            super(id, model);
            final WebMarkupContainer temavelger = new WebMarkupContainer("tema-velger");
            temavelger.setOutputMarkupId(true);
            temavelger.add(new Label("tema"));
            temavelger.add(new AjaxLink<Void>("endre-tema") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    target.add(temavelger);
                }
            });
            TextArea fritekst = new TextArea("fritekst");
            AjaxLink<Void> avbryt = new AjaxLink<Void>("avbryt") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    sideNavigerer.forside();
                    target.add(SendSporsmalPanel.this.getParent());
                }
            };
            AjaxSubmitLink send = new AjaxSubmitLink("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    Sporsmal spsm = getModelObject();
                    sporsmalOgSvarService.opprettSporsmal(new WSSporsmal().withFritekst(spsm.getFritekst()).withTema(spsm.getTema()),
                            fodselsnr);
                    send(getPage(), Broadcast.BREADTH, Innboks.MELDINGER_OPPDATERT);
                    sideNavigerer.neste();
                    target.add(SendSporsmalPanel.this.getParent());
                }
            };
            add(fritekst, temavelger, avbryt, send);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SendSporsmalPanel.class, "textarea.js")));

        }
    }
}
