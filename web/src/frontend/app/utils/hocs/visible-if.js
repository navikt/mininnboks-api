import React, { PropTypes as PT } from 'react';
import { fn, getDisplayName } from './../utils';

export function visibleIf(komponent) {
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
