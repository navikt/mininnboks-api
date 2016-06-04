import React, { PropTypes as pt } from 'react';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import { getValidationMessages } from '../skriv/Skriv';
import { settSkrivSvar, submitSkjema } from './../utils/actions/actions';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';

const avbryt = (dispatch) => () => dispatch(settSkrivSvar(false));

const submit = (dispatch) => () => dispatch(submitSkjema(true));


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
                <input type="submit" className="knapp knapp-hoved knapp-liten" value={formatMessage({ id: 'traadvisning.besvar.send' })} onClick={submit(dispatch)} />
                <a href="#" onClick={avbryt(dispatch)} role="button">
                    {formatMessage({ id: 'traadvisning.besvar.avbryt' })}
                </a>
            </div>
        );
    }
}

BesvarBoks.propTypes = {
    formatMessage: pt.func.isRequired
};

export default injectIntl(connect()(BesvarBoks));
