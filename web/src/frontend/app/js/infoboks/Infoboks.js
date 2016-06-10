import React from 'react';
import Sendingstatus from '../skriv/SendingStatus';
import { injectIntl, FormattedHTMLMessage } from 'react-intl';

class InfoBoks extends React.Component {
    render() {
        const { sendingStatus } = this.props;

        if (sendingStatus === 'IKKE_SENDT') {
            return <noscript/>;
        }

        return (
            <div className={"info-boks " + sendingStatus} aria-live="assertive" aria-atomic="true" role="alert">
                <p className="vekk">{Sendingstatus[sendingStatus]}</p>
                <FormattedHTMLMessage id={"infoboks." + sendingStatus}  />
            </div>
        );
    }
}

export default injectIntl(InfoBoks);