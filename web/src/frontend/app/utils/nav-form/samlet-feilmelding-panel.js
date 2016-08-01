import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';

function SamletFeilmeldingPanel({ errors, submitFailed, submitToken }) {
    if (!submitFailed || Object.keys(errors).length === 0 || submitToken === null) {
        return null;
    }

    const feilmeldinger = Object.entries(errors).map(([element, feilkode]) => (
        <li key={element}>
            <a href={`#${element}`}><FormattedMessage id={`feilmelding.${element}.${feilkode}`} /></a>
        </li>
    ));

    return (
        <div
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
            className="panel panel-feilsammendrag text-left"
            tabIndex="-1"
        >
            <h3 className="typo-undertittel">
                <FormattedMessage
                    id="skriv-sporsmal.feilmelding.header"
                    values={{ antallFeil: feilmeldinger.length }}
                />
            </h3>
            <ul>
                {feilmeldinger}
            </ul>
        </div>
    );
}

SamletFeilmeldingPanel.propTypes = {
    errors: PT.object.isRequired,
    submitFailed: PT.bool.isRequired,
    submitToken: PT.string
};

export default SamletFeilmeldingPanel;
