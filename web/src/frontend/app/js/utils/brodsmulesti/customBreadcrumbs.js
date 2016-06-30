import React, { PropTypes as PT } from 'react';
import { PropTypes as RouterProps } from 'react-router';
import { Link } from 'react-router';
import Breadcrumbs from 'react-router-breadcrumbs';

const createLink = (link, key, text, index, routes) => {
    if (index === routes.length - 1) {
        return <li key={key}><p>{text}</p></li>
    }
    return <li key={key}><Link to={link}>{text}</Link></li>
};
const customBreadcrumb = ({ routes, params, resolver, formatMessage }) => {
    const prefixElement = (
        <li>
            <a href={formatMessage({ id: 'dittnav.url' })}>{formatMessage({ id: 'brodsmulesti.dittnav.lenketekst' })}</a>
        </li>
    );
    return (
        <div className="vekk-mobil">
            <h2 className="vekk vekk-mobil">{formatMessage({ id: 'brodsmulesti.info.for.skjemlesere' })}</h2>
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
};
customBreadcrumb.defaultProps = {
    resolver: (_, t) => t
};
customBreadcrumb.propTypes = {
    routes: PT.arrayOf(RouterProps.route).isRequired,
    params: PT.object,
    resolver: PT.func
};

export function greedyRender(Component) {
    return ({ children, ...props }) => {
        if (children) {
            return children;
        }
        return <Component {...props} />
    }
}


export * from 'react-router-breadcrumbs';
export default customBreadcrumb;
