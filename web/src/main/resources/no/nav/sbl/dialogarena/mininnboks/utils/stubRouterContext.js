var React = require('react');

var stubRouterContext = function (Component) {
    return React.createClass({
        childContextTypes: {
            router: React.PropTypes.func
        },
        getChildContext: function () {
            return {
                router: {
                    makePath: function () {},
                    makeHref: function () {},
                    transitionTo: function () {},
                    replaceWith: function () {},
                    goBack: function () {},
                    getCurrentPath: function () {},
                    getCurrentRoutes: function () {},
                    getCurrentPathname: function () {},
                    getCurrentParams: function () {},
                    getCurrentQuery: function () {},
                    isActive: function () {}
                }
            };
        },

        render: function () {
            return Component;
        }

    });
};

module.exports = stubRouterContext;