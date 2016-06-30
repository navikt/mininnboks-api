import React, { PropTypes as pt } from 'react';

const AntallMeldinger = ({ antall, formatMessage }) => {
    const antallCls = antall === 1 ? 'antall-ikon antall-en' : 'antall-ikon antall-flere';
    let antallTekst = antall === 1 ? '' : antall;
    let flereMeldingerAriaLabel = '';

    if (antall > 1) {
        if (antall < 10) {
            antallTekst = antall;
        } else {
            antallTekst = '9+';
        }

        flereMeldingerAriaLabel =
            <span className="vekk">{`${antallTekst} ${formatMessage({ id: 'meldinger.flere.aria.label' })}`}</span>;
    }

    return (
        <div className={antallCls}>
            <span aria-hidden="true">{antallTekst}</span>
            {flereMeldingerAriaLabel}
        </div>
    );
};

AntallMeldinger.propTypes = {
    antall: pt.number,
    formatMessage: pt.func.isRequired
};

export default AntallMeldinger;
