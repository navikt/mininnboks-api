import React, { PropTypes as pt } from 'react';
import { injectIntl } from 'react-intl';

class SamletFeilmeldingPanel extends React.Component {

    render() {
        const { formatMessage, validationResult  } = this.props;

        if (validationResult.length < 2) {
            return <noscript/>;
        }

        const feilmeldinger = validationResult.map((feilmelding) =>
            <li key={feilmelding}>{formatMessage({ id: 'feilmeldingliste.' + feilmelding })}</li>);

        return (
            <div role="alert" aria-live="assertive" aria-atomic="true" className="panel panel-feilsammendrag venstrestill-tekst">
                <h3>Du må fylle ut følgende</h3>
                <ul>
                    {feilmeldinger}
                </ul>
            </div>
        );
    }
}

SamletFeilmeldingPanel.propTypes = {
    validationResult: pt.array.isRequired
};

export default injectIntl(SamletFeilmeldingPanel);
