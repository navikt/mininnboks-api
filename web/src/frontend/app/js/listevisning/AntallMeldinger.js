import React, { PropTypes as pt } from 'react';

const AntallMeldinger = ({ antall }) => {
        const antallCls = antall === 1 ? 'antall-ikon antall-en' : 'antall-ikon antall-flere';
        const antallTekst = antall === 1 ? null : antall;

        return (
            <div className={antallCls} aria-hidden="true">{antallTekst}</div>
        );
    };

AntallMeldinger.propTypes = {
   antall: pt.number
};

export default AntallMeldinger;
