import React, { PropTypes as PT } from 'react';
import { validate } from '../utils/validationutil';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import { FormattedMessage } from 'react-intl';
import { reduxForm } from 'redux-form';

function BesvarBoks({ traadId, skrivSvar, avbryt, submit, fields, handleSubmit, resetForm }) {
    if (!skrivSvar) {
        return null;
    }
    const onSubmit = ({ fritekst }) => {
        submit(traadId, fritekst);
    };
    const onAbort = () => {
        resetForm();
        avbryt();
    };

    /* eslint-disable jsx-a11y/no-onchange, no-script-url */
    return (
        <form className="besvar-container" onSubmit={handleSubmit(onSubmit)}>
            <ExpandingTextArea config={fields.fritekst} />
            <button type="submit" className="knapp knapp-hoved knapp-liten">
                <FormattedMessage id="traadvisning.besvar.send" />
            </button>
            <a href="javascript:void(0)" onClick={onAbort} role="button" className="svar-avbryt" >
                <FormattedMessage id="traadvisning.besvar.avbryt" />
            </a>
        </form>
    );
}

BesvarBoks.propTypes = {
    traadId: PT.string.isRequired,
    skrivSvar: PT.bool.isRequired,
    submit: PT.func.isRequired,
    resetForm: PT.func.isRequired,
    avbryt: PT.func.isRequired,
    fields: PT.object.isRequired,
    handleSubmit: PT.func.isRequired
};

export default reduxForm({
    form: 'besvar',
    fields: ['fritekst'],
    validate
})(BesvarBoks);
