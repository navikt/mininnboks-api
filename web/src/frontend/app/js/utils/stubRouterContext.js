import React from 'react';
const assign = Object.assign || require('object-assign');

const stubRouterContext = (Component, props, stubs) => {
    function RouterStub() {
    }

    assign(RouterStub, {
        makePath() {
        },
        makeHref() {
        },
        transitionTo() {
        },
        replaceWith() {
        },
        goBack() {
        },
        getCurrentPath() {
        },
        getCurrentRoutes() {
        },
        getCurrentPathname() {
        },
        getCurrentParams() {
        },
        getCurrentQuery() {
        },
        isActive() {
        },
        getRouteAtDepth() {
        },
        getRouteComponentAtDepth() {
        },
        createHref(localtion) {
        }
    }, stubs);

    return React.createClass({
        childContextTypes: {
            router: React.PropTypes.func,
            routeDepth: React.PropTypes.number
        },
        getChildContext() {
            return {
                router: RouterStub,
                routeDepth: 0
            };
        },
        getComponent() {
            return this.refs.component;
        },
        render() {
            return <Component ref="component" {...props} />;
        }
    });
};

export default stubRouterContext;
