import React from 'react';
import Sendingstatus from '../skriv/SendingStatus';
import { injectIntl } from 'react-intl';

class InfoBoks extends React.Component {
    render() {
        const { formatMessage, sendingStatus } = this.props;

        if (sendingStatus === 'IKKE_SENDT') {
            return <noscript/>;
        }

        return (
            <div className={"info-boks " + sendingStatus} aria-live="assertive" aria-atomic="true" role="alert">
                <p className="vekk">{Sendingstatus[sendingStatus]}</p>
                <div dangerouslySetInnerHTML={{__html: formatMessage({id: "infoboks." + sendingStatus})}}></div>
            </div>
        );
    }
}

export default injectIntl(InfoBoks);
