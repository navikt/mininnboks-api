package no.nav.sbl.dialogarena.mininnboks.sporsmal;

import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class VisningsUtils {

    public static AbstractReadOnlyModel<Boolean> numberOfErrorMessages(final AriaFeedbackPanel feedbackPanel, final int numberOfMessages) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return feedbackPanel.numberOfMessages() == numberOfMessages;
            }
        };
    }

    public static IModel<Boolean> componentHasErrors(final Component component, final AriaFeedbackPanel feedbackPanel) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                for (FeedbackMessage message : feedbackPanel.getMessages()) {
                    Component reporter = message.getReporter();

                    if (reporter.equals(component)){
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
