import React, { PropTypes as PT } from 'react';
import { Print, createDokumentUrl } from 'react-dokumentvisning';
import { injectIntl, intlShape } from 'react-intl';

const PrintPage = ({ params: { journalpostid, dokumentreferanse }, intl: { formatMessage } }) => {
    const url = createDokumentUrl(formatMessage({ id: 'saksoversikt.link' }), journalpostid, dokumentreferanse);
    return <Print documentUrl={url} />;
};

PrintPage.propTypes = {
    intl: intlShape,
    params: PT.shape({
        journalpostId: PT.string,
        dokumentreferanse: PT.string
    }).isRequired
};

export default injectIntl(PrintPage);
