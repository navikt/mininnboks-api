import React, { PropTypes as PT } from 'react';
import { validate } from '../utils/validationutil';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import { STATUS } from './../ducks/utils';
import { FormattedMessage } from 'react-intl';
import { reduxForm } from 'redux-form';
import { visibleIfHOC } from './../utils/hocs/visible-if';
import { Hovedknapp } from 'nav-react-design/dist/knapp';

function BesvarBoks({ traadId, avbryt, innsendingStatus, submit, fields, handleSubmit, resetForm }) {
    const onSubmit = ({ fritekst }) => {
        submit(traadId, fritekst);
    };
    const onAbort = () => {
        resetForm();
        avbryt();
    };

    /* eslint-disable jsx-a11y/no-onchange, no-script-url */
    return (
        <form className="besvar-container text-center blokk-center blokk-l" onSubmit={handleSubmit(onSubmit)}>
            <ExpandingTextArea config={fields.fritekst} className="blokk-m" />
            <div className="blokk-xs">
                <Hovedknapp type="submit" spinner={innsendingStatus === STATUS.PENDING}>
                    <FormattedMessage id="traadvisning.besvar.send" />
                </Hovedknapp>
            </div>
            <a href="javascript:void(0)" onClick={onAbort} role="button" >
                <FormattedMessage id="traadvisning.besvar.avbryt" />
            </a>
        </form>
    );
}

BesvarBoks.propTypes = {
    innsendingStatus: PT.string.isRequired,
    traadId: PT.string.isRequired,
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
})(visibleIfHOC(BesvarBoks));
