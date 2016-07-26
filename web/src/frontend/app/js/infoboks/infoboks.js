import React, { PropTypes as PT } from 'react';
import Sendingstatus from '../skriv-nytt-sporsmal/sending-status';
import { FormattedHTMLMessage } from 'react-intl';
import classNames from 'classnames';

function InfoBoks({ sendingStatus }) {
    if (sendingStatus === 'IKKE_SENDT' || sendingStatus === undefined) {
        return null;
    }

    const klassenavn = classNames('info-boks', sendingStatus);

    return (
        <div className={klassenavn} aria-live="assertive" aria-atomic="true" role="alert">
            <p className="vekk">{Sendingstatus[sendingStatus]}</p>
            <FormattedHTMLMessage id={`infoboks.${sendingStatus}`} />
        </div>
    );
}

InfoBoks.propTypes = {
    sendingStatus: PT.string
};

export default InfoBoks;
