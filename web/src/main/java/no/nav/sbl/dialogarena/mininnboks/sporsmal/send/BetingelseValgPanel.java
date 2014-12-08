package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.VisningsUtils.componentHasErrors;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.VisningsUtils.numberOfErrorMessages;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage.IKKE_AKSEPTERT_FEILMELDING_PROPERTY;

public class BetingelseValgPanel extends Panel {

    private final Label checkboxFeilmelding;

    public BetingelseValgPanel(String id, IModel<Sporsmal> model, AriaFeedbackPanel feedbackPanel) {
        super(id);
        setOutputMarkupId(true);

        PropertyModel<Boolean> vilkarAkseptertModel = new PropertyModel<>(model, "betingelserAkseptert");

        AjaxCheckBox checkbox = new AjaxCheckBox("betingelserCheckbox", vilkarAkseptertModel) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(this);
            }
        };
        checkbox.add(new IValidator<Boolean>() {
            @Override
            public void validate(IValidatable<Boolean> validatable) {
                if (!validatable.getValue()) {
                    validatable.error(new ValidationError(getString(IKKE_AKSEPTERT_FEILMELDING_PROPERTY)));
                }
            }
        });

        checkboxFeilmelding = new Label("checkbox-feilmelding", "Du må godta vilkårene for å sende spørsmålet");
        checkboxFeilmelding.setOutputMarkupPlaceholderTag(true);
        checkboxFeilmelding.add(visibleIf(both(
                numberOfErrorMessages(feedbackPanel, 1))
                .and(componentHasErrors(checkbox, feedbackPanel))));
        add(checkboxFeilmelding);

        add(checkbox.setOutputMarkupId(true));

        add(new AjaxLink<Void>("visBetingelser") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('.betingelser').dialog('open');");
            }
        });
    }

    public void oppdater(AjaxRequestTarget target) {
        target.add(checkboxFeilmelding);
    }
}
