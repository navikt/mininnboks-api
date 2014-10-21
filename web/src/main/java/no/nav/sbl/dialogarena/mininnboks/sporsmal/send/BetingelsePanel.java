package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class BetingelsePanel extends Panel {

    public static final String BETINGELSER_BESVART = "sendsporsmal.betingelser.besvart";

    private final IModel<Boolean> model;
    private final ModalWindow modalWindow;

    public BetingelsePanel(String id, final IModel<Boolean> model, final ModalWindow modalWindow) {
        super(id);
        this.model = model;
        this.modalWindow = modalWindow;

        add(new AjaxLink<Void>("aksept") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                svarOgLukkPanel(target, true);
            }
        });
        add(new AjaxLink<Void>("ikkeAksept") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                svarOgLukkPanel(target, false);
            }
        });
    }

    private void svarOgLukkPanel(AjaxRequestTarget target, boolean aksept) {
        model.setObject(aksept);
        modalWindow.close(target);
        send(this, Broadcast.BUBBLE, BETINGELSER_BESVART);
    }
}
