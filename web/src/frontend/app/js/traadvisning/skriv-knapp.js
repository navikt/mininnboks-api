import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';

function SkrivKnapp({ onClick, kanBesvares, skrivSvar }) {
    if (!kanBesvares || (kanBesvares && skrivSvar)) {
        return null;
    }

    return (
        <div className="innboks-navigasjon">
            <button onClick={onClick} className="knapp knapp-hoved knapp-liten">
                <FormattedMessage id="traadvisning.skriv.svar.link" />
            </button>
        </div>
    );
}

SkrivKnapp.propTypes = {
    onClick: PT.func.isRequired,
    kanBesvares: PT.bool.isRequired,
    skrivSvar: PT.bool.isRequired
};

export default SkrivKnapp;
