package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class AriaFeedbackPanel extends FeedbackPanel {
    public AriaFeedbackPanel(String id) {
        super(id);
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        Component component = super.newMessageDisplayComponent(id, message);
        component.add(AttributeAppender.append("id", "aria-error-"+message.getReporter().getMarkupId()));
        return component;
    }
}