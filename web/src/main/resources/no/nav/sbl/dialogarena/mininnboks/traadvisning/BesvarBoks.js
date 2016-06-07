import React, { PropTypes as pt } from 'react';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import { validate, getValidationMessages } from '../validation/validationutil';
import { settSkrivSvar, resetInputState, settSendingStatus, submitSkjema } from './../utils/actions/actions';
import SendingStatus from './../skriv/SendingStatus';
import { injectIntl, intlShape } from 'react-intl';
import { addXsrfHeader } from '../utils/Utils';
import { connect } from 'react-redux';

const avbryt = (dispatch) => () => dispatch(resetInputState());

const submit = (dispatch, temagruppe, sporsmalInputtekst) => () => {
    dispatch(submitSkjema(true));
    if (validate(true, sporsmalInputtekst, true)) {
        $.ajax({
                type: 'POST',
                url: '/mininnboks/tjenester/traader/sporsmal',
                contentType: 'application/json',
                data: JSON.stringify({ temagruppe, sporsmalInputtekst }),
                beforeSend: addXsrfHeader
            })
            .done(function (response, status, xhr) {
                if (xhr.status !== 201) {
                    dispatch(settSendingStatus(SendingStatus.feil));
                } else {
                    dispatch(settSendingStatus(SendingStatus.ok));
                }
            }.bind(this))
            .fail(function () {
                dispatch(settSendingStatus(SendingStatus.feil));
            }.bind(this));
    }
};


class BesvarBoks extends React.Component {

    render() {
        const { dispatch, formatMessage, sporsmalInputtekst, skrivSvar, harSubmittedSkjema } = this.props;
        const validationResult = getValidationMessages(harSubmittedSkjema, sporsmalInputtekst, true);

        if (!skrivSvar) {
            return <noscript/>;
        }

        return (
            <div className="besvar-container">
                <ExpandingTextArea formatMessage={formatMessage} sporsmalInputtekst={sporsmalInputtekst} validationResult={validationResult}/>
                <input type="submit" className="knapp knapp-hoved knapp-liten" value={formatMessage({ id: 'traadvisning.besvar.send' })}
                       onClick={submit(dispatch, 'ARBD', sporsmalInputtekst)} />
                <a href="#" onClick={avbryt(dispatch)} role="button">
                    {formatMessage({ id: 'traadvisning.besvar.avbryt' })}
                </a>
            </div>
        );
    }
}

BesvarBoks.propTypes = {
    formatMessage: pt.func.isRequired,
    sporsmalInputtekst: pt.string.isRequired
};

export default injectIntl(connect()(BesvarBoks));
