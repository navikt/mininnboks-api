import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { visibleIfHOC } from './../utils/hocs/visible-if';
import { Hovedknapp } from 'nav-react-design/dist/knapp';

function SkrivKnapp({ onClick }) {
    return (
        <div className="text-center blokk-l">
            <Hovedknapp onClick={onClick} storrelse="liten">
                <FormattedMessage id="traadvisning.skriv.svar.link" />
            </Hovedknapp>
        </div>
    );
}

SkrivKnapp.propTypes = {
    onClick: PT.func.isRequired
};

export default visibleIfHOC(SkrivKnapp);
