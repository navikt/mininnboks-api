import React, { PropTypes as pt } from 'react';
import { FormattedMessage } from 'react-intl';

const AntallMeldinger = ({ antall }) => {
    const antallCls = antall === 1 ? 'antall-ikon antall-en' : 'antall-ikon antall-flere';
    let antallTekst = antall === 1 ? '' : null;
    let flereMeldingerAriaLabel = null;

    if (antall > 1) {
        if (antall < 10) {
            antallTekst = antall;
        } else {
            antallTekst = '9+';
        }

        flereMeldingerAriaLabel = (
            <FormattedMessage id="meldinger.flere.aria.label" values={{ antall }} />
        );
    }

    return (
        <div className={antallCls}>
            <span aria-hidden="true">{antallTekst}</span>
            <span className="vekk">{flereMeldingerAriaLabel}</span>
        </div>
    );
};

AntallMeldinger.propTypes = {
    antall: pt.number
};

export default AntallMeldinger;
