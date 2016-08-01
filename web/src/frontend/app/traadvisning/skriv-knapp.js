import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { visibleIf } from './../utils/hocs/visible-if';

function SkrivKnapp({ onClick }) {
    return (
        <div className="text-center blokk-l">
            <button onClick={onClick} className="knapp knapp-hoved knapp-liten">
                <FormattedMessage id="traadvisning.skriv.svar.link" />
            </button>
        </div>
    );
}

SkrivKnapp.propTypes = {
    onClick: PT.func.isRequired
};

export default visibleIf(SkrivKnapp);
