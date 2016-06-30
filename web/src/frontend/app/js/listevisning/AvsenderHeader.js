import React, { PropTypes as pt } from 'react';

const AvsenderHeader = ({ formatMessage, meldingType }) => {
    const avsender = <span className="avsender-fra-nav">{formatMessage({ id: 'avsender.tekst.NAV' })}</span>;

    const maaBehandlesStatus = meldingType === 'SPORSMAL_MODIA_UTGAAENDE' ?
        <span>/ <strong className="purring">{formatMessage({ id: 'purre.svar' })}</strong></span> : <noscript/>;

    return <span>/ Fra {avsender} {maaBehandlesStatus}</span>;
};

AvsenderHeader.propTypes = {
    formatMessage: pt.func.isRequired,
    meldingType: pt.string.isRequired
};

export default AvsenderHeader;
