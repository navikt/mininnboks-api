import React, { Component } from 'react';

const stubRouterContext = (comp, props, stubs) => {
    function RouterStub() {
    }

    Object.assign(RouterStub, {
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
        createHref() {
        }
    }, stubs);

    class StubClass extends Component {
        getChildContext() {
            return {
                router: RouterStub,
                routeDepth: 0
            };
        }

        getComponent() {
            return this.refs.component;
        }

        render() {
            return <comp ref="component" {...props} />;
        }
    }
    StubClass.childContextTypes = {
        router: React.PropTypes.func,
        routeDepth: React.PropTypes.number
    };

    return StubClass;
};

export default stubRouterContext;
