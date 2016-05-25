import React from 'react';
var assign = Object.assign || require('object-assign');

var stubRouterContext = function (Component, props, stubs) {
    function RouterStub() {
    }

    assign(RouterStub, {
        makePath: function () {
        },
        makeHref: function () {
        },
        transitionTo: function () {
        },
        replaceWith: function () {
        },
        goBack: function () {
        },
        getCurrentPath: function () {
        },
        getCurrentRoutes: function () {
        },
        getCurrentPathname: function () {
        },
        getCurrentParams: function () {
        },
        getCurrentQuery: function () {
        },
        isActive: function () {
        },
        getRouteAtDepth: function () {
        },
        getRouteComponentAtDepth: function () {
        }
    }, stubs);

    return React.createClass({
        childContextTypes: {
            router: React.PropTypes.func,
            routeDepth: React.PropTypes.number
        },
        getChildContext: function () {
            return {
                router: RouterStub,
                routeDepth: 0
            };
        },
        getComponent: function () {
            return this.refs.component;
        },
        render: function () {
            return <Component ref="component" {...props} />;
        }
    });
};

export default stubRouterContext;