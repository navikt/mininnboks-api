require('../testConfig');
var chai = require('chai');
var expect = chai.expect;
var assert = chai.assert;
var sinon = require('sinon');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var stubRouterContext = require('../utils/stubRouterContext');

var Skriv = require('./Skriv');

var resourcesMock = {
    get: function () {
        return '';
    }
};

var SkrivWrapper = stubRouterContext(Skriv, {resources: resourcesMock, params: {temagruppe: 'ARBD'}});

describe('Skriv Page', function () {

    var skriv = TestUtils.renderIntoDocument(<SkrivWrapper />);
    var sendKnapp = TestUtils.findRenderedDOMComponentWithClass(skriv, 'send-link');

    before(function () {
        sinon.spy($, 'ajax');
    });

    after(function () {
        $.ajax.restore();
    });

    it('kan ikke sende uten å ha fylt ut tekst i tekstfeltet og godtatt vilkår', function () {
        sinon.spy(skriv.getComponent().refs.form, 'validate')

        TestUtils.Simulate.click(sendKnapp);

        assert.isTrue(skriv.getComponent().refs.form.validate.called);
        assert.isFalse($.ajax.called);
        assert.isFalse(skriv.getComponent().state.sendt);

        skriv.getComponent().refs.form.validate.restore();
    });

    it('kan sende når det er tekst i tekstfeltet og vilkår er godatt', function () {
        sinon.spy(skriv.getComponent().refs.form, 'validate')
        var server = sinon.fakeServer.create();
        server.respondWith([200, {}, 'From fake server']);

        var textarea = TestUtils.findRenderedDOMComponentWithTag(skriv, 'textarea');
        var checkbox = TestUtils.findRenderedDOMComponentWithClass(skriv, 'betingelseCheckboks');

        TestUtils.Simulate.change(textarea, {target: {value: 'Text'}});
        TestUtils.Simulate.change(checkbox, {target: {checked: true}});
        TestUtils.Simulate.click(sendKnapp);

        assert.isTrue(skriv.getComponent().refs.form.validate.called);
        assert.isTrue($.ajax.calledOnce);
        server.respond();
        expect(skriv.getComponent().state.sendt).to.equal(true);

        server.restore();
    });
});
