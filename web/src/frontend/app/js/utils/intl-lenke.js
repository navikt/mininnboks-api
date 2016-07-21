import React, { PropTypes as PT } from 'react';
import { injectIntl } from 'react-intl';

function IntlLenke({ href, intl, children, ...props }) {
    return (
        <a href={intl.formatMessage({ id: href })} {...props}>
            {children}
        </a>
    );
}

IntlLenke.propTypes = {
    intl: PT.object.isRequired,
    href: PT.string.isRequired,
    children: PT.node.isRequired
};

export default injectIntl(IntlLenke);
