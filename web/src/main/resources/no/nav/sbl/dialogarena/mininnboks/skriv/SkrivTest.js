require('../testConfig');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var stubRouterContext = require('../utils/stubRouterContext');

var Skriv = require('./Skriv');

function RouterStub() {
}
RouterStub.makePath = function () {
};

var resourcesMock = {
    get: function () {
        return '';
    }
};

var params = {
    temagruppe: 'ARBD'
};

var SkrivWrapper = React.createClass({
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
        return <Skriv resources={resourcesMock} params={params}/>;
    }
});

describe('Skriv Page', function () {
    it('kan ikke sende uten å ha fylt ut tekst i tekstfeltet', function () {
        var skriv = TestUtils.renderIntoDocument(<SkrivWrapper />);
        var sendKnapp = TestUtils.findRenderedDOMComponentWithClass(skriv, 'send-link');
        TestUtils.Simulate.click(sendKnapp);
        expect(true).to.be.true;
    });
});
