import React, { PropTypes as PT } from 'react';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import { FormattedMessage } from 'react-intl';
import { getValidationMessages } from '../validation/validationutil';

function BesvarBoks({ traadId, fritekst, skrivSvar, harSubmittedSkjema, skrivTekst, avbryt, submit }) {
    if (!skrivSvar) {
        return null;
    }
    const validationResult = getValidationMessages(harSubmittedSkjema, fritekst, true);
    const onSubmit = (event) => {
        event.preventDefault();
        submit(traadId, fritekst);
    };

    return (
        <form className="besvar-container" onSubmit={onSubmit}>
            <ExpandingTextArea
                fritekst={fritekst}
                validationResult={validationResult}
                onChange={(event) => skrivTekst(event.target.value)}
            />
            <button type="submit" className="knapp knapp-hoved knapp-liten">
                <FormattedMessage id="traadvisning.besvar.send" />
            </button>
            <a href="javascript:void(0)" onClick={avbryt} role="button" className="svar-avbryt" >
                <FormattedMessage id="traadvisning.besvar.avbryt" />
            </a>
        </form>
    );
}

BesvarBoks.propTypes = {
    traadId: PT.string.isRequired,
    fritekst: PT.string.isRequired,
    skrivSvar: PT.bool.isRequired,
    harSubmittedSkjema: PT.bool.isRequired,
    skrivTekst: PT.func.isRequired,
    submit: PT.func.isRequired,
    avbryt: PT.func.isRequired
};

export default BesvarBoks;
