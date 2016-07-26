import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';

function AvsenderHeader({ meldingType }) {
    const avsender = <span className="avsender-fra-nav"><FormattedMessage id="avsender.tekst.NAV" /></span>;

    const maaBehandlesStatus = meldingType === 'SPORSMAL_MODIA_UTGAAENDE' ?
        <span>/ <strong className="purring"><FormattedMessage id="purre.svar" /></strong></span> : null;

    return <span>/ Fra {avsender} {maaBehandlesStatus}</span>;
}

AvsenderHeader.propTypes = {
    meldingType: PT.string.isRequired
};

export default AvsenderHeader;
