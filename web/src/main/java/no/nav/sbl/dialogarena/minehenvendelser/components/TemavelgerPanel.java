package no.nav.sbl.dialogarena.minehenvendelser.components;

import java.util.List;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class TemavelgerPanel extends Panel {

    SporsmalOgSvarModel model;
    private final WebMarkupContainer container;

    public TemavelgerPanel(String id, final List<String> alleTema, final SporsmalOgSvarModel model, final NesteSide nesteSide) {
        super(id, model);
        this.model = model;
        container = new WebMarkupContainer("tema-container");
        container.setOutputMarkupId(true);
        container.add(new TemaListe("tema-liste", alleTema));
        AjaxLink<Void> fortsettLink = new AjaxLink<Void>("fortsett") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                nesteSide.neste();
                target.add(TemavelgerPanel.this.getParent());
            }
        };
        fortsettLink.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return alleTema.contains(model.getObject().tema);
            }
        }));
        container.add(fortsettLink);
        add(container);
    }

    private class TemaListe extends PropertyListView<String> {

//        ListItem<String> current;

        public TemaListe(String id, List<String> alleTema) {
            super(id, alleTema);
        }

        @Override
        protected void populateItem(final ListItem<String> item) {
            item.add(new Label("tema", item.getModelObject()));
            item.add(hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return item.getModelObject().equals(model.getObject().tema);
                }
            }));
            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    model.setTema(item.getModelObject());
                    target.add(container);
//                    target.add(item);
//                    if (current != null && current != item) {
//                        target.add(current);
//                    }
//                    current = item;
                }
            });
        }
    }

}
