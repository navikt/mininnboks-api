import React, { PropTypes as PT } from 'react';
import DokumentPreview from './dokument-preview';
import MeldingPreview from './melding-preview';

function TraadPreview(props) {
    const type = props.traad.nyeste.type;

    if (type === 'DOKUMENT_VARSEL') {
        return <DokumentPreview {...props} />;
    }
    return <MeldingPreview {...props} />;
}


TraadPreview.propTypes = {
    traad: PT.object
};

export default TraadPreview;
