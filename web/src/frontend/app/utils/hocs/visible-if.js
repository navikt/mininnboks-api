import React, { PropTypes as PT } from 'react';
import { fn, getDisplayName } from '../../utils';

function VisibleIf({ visibleIf, children }) {
    if (fn(visibleIf)()) {
        return <div>{children}</div>;
    }
    return null;
}

VisibleIf.propTypes = {
    visibleIf: PT.oneOfType([PT.func, PT.bool]).isRequired,
    children: PT.node.isRequired
};

export default VisibleIf;

export function visibleIfHOC(komponent) {
    function visibleIfWrapper({ visibleIf, ...props }) {
        if (fn(visibleIf)()) {
            return React.createElement(komponent, props);
        }
        return null;
    }

    visibleIfWrapper.propTypes = {
        visibleIf: PT.oneOfType([PT.func, PT.bool]).isRequired
    };
    visibleIfWrapper.displayName = `visibleIfWrapper(${getDisplayName(komponent)})`;

    return visibleIfWrapper;
}
