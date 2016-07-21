import React, { PropTypes as PT } from 'react';
import { Link } from 'react-router';
import { FormattedMessage } from 'react-intl';
import Breadcrumbs from 'react-router-breadcrumbs';
import IntlLenke from './../intl-lenke';

const createLink = (link, key, text, index, routes) => {
    if (index === routes.length - 1) {
        return <li key={key}><p>{text}</p></li>;
    }
    return <li key={key}><Link to={link}>{text}</Link></li>;
};

const prefixElement = (
    <li key="dittnav">
        <IntlLenke href="dittnav.url">
            <FormattedMessage id="brodsmulesti.dittnav.lenketekst" />
        </IntlLenke>
    </li>
);

function CustomBreadcrumb({ routes, params, resolver }) {
    return (
        <div className="vekk-mobil">
            <h2 className="vekk vekk-mobil">
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
    resolver: PT.func
};

export * from 'react-router-breadcrumbs';

export default CustomBreadcrumb;
