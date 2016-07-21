import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';

function SamletFeilmeldingPanel({ validationResult }) {
    if (validationResult.length < 2) {
        return null;
    }

    const feilmeldinger = validationResult.map((feilmelding) => (
        <li key={feilmelding}><FormattedMessage id={`feilmeldingliste.${feilmelding}`} /></li>
    ));

    return (
        <div
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
            className="panel panel-feilsammendrag venstrestill-tekst"
        >
            <h3><FormattedMessage id="skriv-sporsmal.feilmelding.header" /></h3>
            <ul>
                {feilmeldinger}
            </ul>
        </div>
    );
}

SamletFeilmeldingPanel.propTypes = {
    validationResult: PT.array.isRequired
};

export default SamletFeilmeldingPanel;
