import React, { PropTypes as PT } from 'react';
import { visibleIfHOC } from './../hocs/visible-if';

function InlineFeilmelding({ id, children }) {
    return (
        <span
            id={id}
            className="skjema-feilmelding"
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
        >
            {children}
        </span>
    );
}

InlineFeilmelding.propTypes = {
    id: PT.string.isRequired,
    children: PT.node.isRequired
};

export default visibleIfHOC(InlineFeilmelding);
