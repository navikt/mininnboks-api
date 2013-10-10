package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class TemavelgerPanel extends Panel {

    IModel<Sporsmal> model;
    private final SideNavigerer sideNavigerer;

    public TemavelgerPanel(String id, final List<Tema> alleTema, final IModel<Sporsmal> model, final SideNavigerer sideNavigerer) {
        super(id, model);

        this.model = model;
        this.sideNavigerer = sideNavigerer;

        WebMarkupContainer container = new WebMarkupContainer("tema-container");
        container.add(new TemaListe("tema-liste", alleTema));
        Link avbrytLink = new Link("avbryt") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        };
        container.add(avbrytLink);
        add(container);
    }

    private class TemaListe extends PropertyListView<Tema> {

        public TemaListe(String id, List<Tema> alleTema) {
            super(id, alleTema);
        }

        @Override
        protected void populateItem(final ListItem<Tema> item) {
            item.add(new Label("tema", new StringResourceModel(item.getModelObject().toString(), this, null)));
            item.add(hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return item.getModelObject() == model.getObject().getTema();
                }
            }));
            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    Sporsmal sos = model.getObject();
                    sos.setTema(item.getModelObject());
                    model.setObject(sos);
                    sideNavigerer.neste();
                    target.add(TemavelgerPanel.this.getParent());
                }
            });
        }
    }
}
