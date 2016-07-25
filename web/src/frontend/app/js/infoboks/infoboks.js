import React, { PropTypes as PT } from 'react';
import Sendingstatus from '../skriv-nytt-sporsmal/sending-status';
import { injectIntl, FormattedHTMLMessage } from 'react-intl';

function InfoBoks({ sendingStatus }) {
    if (sendingStatus === 'IKKE_SENDT' || sendingStatus === undefined) {
        return null;
    }

    return (
        <div className={`info-boks ${sendingStatus}`} aria-live="assertive" aria-atomic="true" role="alert">
            <p className="vekk">{Sendingstatus[sendingStatus]}</p>
            <FormattedHTMLMessage id={`infoboks.${sendingStatus}`} />
        </div>
    );
}

InfoBoks.propTypes = {
    sendingStatus: PT.string
};

export default injectIntl(InfoBoks);
