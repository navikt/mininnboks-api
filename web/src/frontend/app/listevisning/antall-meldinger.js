import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import classNames from 'classnames';

const AntallMeldinger = ({ antall }) => {
    const antallCls = classNames('antall-ikon', {
        'antall-en': antall === 1,
        'antall-flere': antall > 1
    });
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
    antall: PT.number
};

export default AntallMeldinger;
