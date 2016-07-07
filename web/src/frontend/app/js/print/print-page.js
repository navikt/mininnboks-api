import React, { PropTypes as pt } from 'react'
import { Print, createDokumentUrl } from 'react-dokumentvisning';
import { injectIntl, intlShape } from 'react-intl';

const PrintPage = ({ params: { journalpostid, dokumentreferanse }, intl: { formatMessage } }) => {
    const url = createDokumentUrl(formatMessage({ id: 'saksoversikt.link' }), journalpostid, dokumentreferanse);
    return <Print documentUrl={url}/>;
};

PrintPage.PropTypes = {
    intl: intlShape,
    params: pt.object
};

export default injectIntl(PrintPage);
