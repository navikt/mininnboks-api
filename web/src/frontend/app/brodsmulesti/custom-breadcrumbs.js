import React, { PropTypes as PT } from 'react';
import { Link } from 'react-router';
import { FormattedMessage } from 'react-intl';
import Breadcrumbs from 'react-router-breadcrumbs';
import IntlLenke from './../utils/intl-lenke';
import classNames from 'classnames';

const createLink = (link, key, text, index, routes) => {
    if (index === routes.length - 1) {
        return <li key={key} className="brodsmulesti-fragment"><p>{text}</p></li>;
    }
    return <li key={key} className="brodsmulesti-fragment"><Link to={link}>{text}</Link></li>;
};

const prefixElement = (
    <li key="dittnav" className="brodsmulesti-fragment">
        <IntlLenke href="dittnav.url">
            <FormattedMessage id="brodsmulesti.dittnav.lenketekst" />
        </IntlLenke>
    </li>
);

function CustomBreadcrumb({ routes, params, resolver, className }) {
    return (
        <div className={classNames('vekk-mobil blokk-s', className)}>
            <h2 className="vekk">
                <FormattedMessage id="brodsmulesti.info.for.skjemlesere" />
            </h2>
            <Breadcrumbs
                routes={routes}
                params={params}
                wrappingComponent="ul"
                className="brodsmulesti"
                resolver={resolver}
                createLink={createLink}
                createSeparator=""
                prefixElements={prefixElement}
            />
        </div>);
}

CustomBreadcrumb.defaultProps = {
    resolver: (_, tekst) => tekst
};

CustomBreadcrumb.propTypes = {
    routes: PT.arrayOf(PT.object).isRequired,
    params: PT.object,
    resolver: PT.func,
    className: PT.string
};

export * from 'react-router-breadcrumbs';

export default CustomBreadcrumb;
