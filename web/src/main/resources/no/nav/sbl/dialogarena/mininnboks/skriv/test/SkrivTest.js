/* eslint-env mocha */

import '../../testConfig';
import React from 'react';
import sinon from 'sinon';
import { expect, assert } from 'chai';
import stubRouterContext from '../../utils/stubRouterContext';
import Skriv from '../Skriv';

import { renderIntoDocument, createRenderer, findRenderedDOMComponentWithTag, findRenderedDOMComponentWithClass } from 'react-addons-test-utils';

var resourcesMock = {
    get: function () {
        return '';
    }
};

var SkrivWrapper = stubRouterContext(Skriv, {resources: resourcesMock, params: {temagruppe: 'ARBD'}});

describe('Skriv Page', function () {
    //
    // var skriv = renderIntoDocument(<SkrivWrapper />);
    // var sendKnapp = findRenderedDOMComponentWithClass(skriv, 'send-link');
    // console.log(sendKnapp);
    //
    // before(function () {
    //     sinon.spy($, 'ajax');
    // });
    //
    // after(function () {
    //     $.ajax.restore();
    // });
    //
    // it('kan ikke sende uten å ha fylt ut tekst i tekstfeltet og godtatt vilkår', function () {
    //     sinon.spy(skriv.getComponent().refs.form, 'validate');
    //
    //     TestUtils.Simulate.click(sendKnapp);
    //
    //     assert.isTrue(skriv.getComponent().refs.form.validate.called);
    //     assert.isFalse($.ajax.called);
    //     assert.isFalse(skriv.getComponent().state.sendt);
    //
    //     skriv.getComponent().refs.form.validate.restore();
    // });
    //
    // it('kan sende når det er tekst i tekstfeltet og vilkår er godatt', function () {
    //     sinon.spy(skriv.getComponent().refs.form, 'validate');
    //     var server = sinon.fakeServer.create();
    //     server.respondWith([200, {}, 'From fake server']);
    //
    //     var textarea = findRenderedDOMComponentWithTag(skriv, 'textarea');
    //     var checkbox = findRenderedDOMComponentWithClass(skriv, 'betingelseCheckboks');
    //
    //     TestUtils.Simulate.change(textarea, {target: {value: 'Text'}});
    //     TestUtils.Simulate.change(checkbox, {target: {checked: true}});
    //     TestUtils.Simulate.click(sendKnapp);
    //
    //     assert.isTrue(skriv.getComponent().refs.form.validate.called);
    //     assert.isTrue($.ajax.calledOnce);
    //     server.respond();
    //     expect(skriv.getComponent().state.sendt).to.equal(true);
    //
    //     server.restore();
    // });
});
