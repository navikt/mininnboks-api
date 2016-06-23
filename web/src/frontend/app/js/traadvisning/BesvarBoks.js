import React, { PropTypes as pt } from 'react';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import { validate, getValidationMessages } from '../validation/validationutil';
import { resetInputState, sendSvar, submitSkjema } from '../utils/actions/actions';
import SendingStatus from '../skriv/SendingStatus';
import { injectIntl, intlShape } from 'react-intl';
import { addXsrfHeader } from '../utils/Utils';
import { connect } from 'react-redux';

const avbryt = (dispatch) => () => dispatch(resetInputState());

const submit = (dispatch, traadId, fritekst) => () => {
    dispatch(submitSkjema(true));
    if (validate(true, fritekst, true)) {
       dispatch(sendSvar(traadId, fritekst));
    }
};

class BesvarBoks extends React.Component {

    render() {
        const { traadId, dispatch, formatMessage, fritekst, skrivSvar, harSubmittedSkjema } = this.props;
        const validationResult = getValidationMessages(harSubmittedSkjema, fritekst, true);
        if (!skrivSvar) {
            return <noscript/>;
        }

        return (
            <div className="besvar-container">
                <ExpandingTextArea formatMessage={formatMessage} fritekst={fritekst} validationResult={validationResult}/>
                <input type="submit" className="knapp knapp-hoved knapp-liten" value={formatMessage({ id: 'traadvisning.besvar.send' })}
                       onClick={submit(dispatch, traadId, fritekst)} />
                <a href="#" onClick={avbryt(dispatch)} role="button">
                    {formatMessage({ id: 'traadvisning.besvar.avbryt' })}
                </a>
            </div>
        );
    }
}

BesvarBoks.propTypes = {
    formatMessage: pt.func.isRequired,
    fritekst: pt.string.isRequired
};

export default injectIntl(connect()(BesvarBoks));
