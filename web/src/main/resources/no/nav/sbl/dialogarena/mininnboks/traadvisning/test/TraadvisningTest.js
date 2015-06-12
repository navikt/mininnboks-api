require('../../testConfig');
var chai = require('chai');
var expect = chai.expect;
var assert = chai.assert;
var sinon = require('sinon');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var stubRouterContext = require('../../utils/stubRouterContext');
var assign = Object.assign || require('object-assign');

var Traadvisning = require('../Traadvisning');

var resourcesMock = {
    get: function () {
        return '';
    }
};

var props = {resources: resourcesMock};

var meldinger = [{
    id: "1",
    traadId: "1",
    fritekst: "",
    kanal: null,
    eksternAktor: null,
    tilknyttetEnhet: null,
    temagruppeNavn: "Familie",
    statusTekst: "Spørsmål fra NAV – Familie",
    type: "SPORSMAL_MODIA_UTGAAENDE",
    temagruppe: "FMLI",
    opprettet: "2015-04-13T12:37:26.152Z",
    avsluttet: null,
    fraNav: true,
    fraBruker: false,
    kassert: false,
    lestDato: "2015-04-17T08:44:34.331Z",
    lest: true
}, {
    id: "2",
    traadId: "1",
    fritekst: "",
    kanal: null,
    eksternAktor: null,
    tilknyttetEnhet: null,
    temagruppeNavn: "Familie",
    statusTekst: "Spørsmål fra NAV – Familie",
    type: "SPORSMAL_MODIA_UTGAAENDE",
    temagruppe: "FMLI",
    opprettet: "2015-04-12T12:37:26.152Z",
    avsluttet: null,
    fraNav: true,
    fraBruker: false,
    kassert: false,
    lestDato: "2015-04-15T08:44:34.331Z",
    lest: true
}];
var traad = {
    traadId: "1",
    meldinger: meldinger,
    nyeste: meldinger[0],
    kanBesvares: true,
    avsluttet: false
};

describe('Trådvisning initialisering', function () {

    before(function () {
        sinon.spy($, 'ajax');
    });

    after(function () {
        $.ajax.restore();
    });

    it('kan initialiseres med trådobjekt', function () {
        var TraadvisningWrapper = stubRouterContext(Traadvisning, assign({}, props, {valgtTraad: traad}));
        var traadvisning = TestUtils.renderIntoDocument(<TraadvisningWrapper />);

        assert.isTrue(traadvisning.getComponent().state.hentet);
        expect(traadvisning.getComponent().state.traad.traadId).to.equal("1");
    });

    it('kan initialiseres med trådid parameter', function () {
        var server = sinon.fakeServer.create();
        server.respondWith(/\/mininnboks\/tjenester\/traader\/(\d+)/, function (xhr, traadId) {
            console.log(traadId);
            expect(traadId).to.equal("1");
            xhr.respond(200, {}, traad);
        });

        var TraadvisningWrapper = stubRouterContext(Traadvisning, assign({}, props, {params: {traadId: "1"}}));
        var traadvisning = TestUtils.renderIntoDocument(<TraadvisningWrapper />);

        server.restore();
    });
});

describe('Trådvisning', function () {
    it('viser tråd', function () {
        var TraadvisningWrapper = stubRouterContext(Traadvisning, assign({}, props, {valgtTraad: assign({}, traad)}));
        var traadvisning = TestUtils.renderIntoDocument(<TraadvisningWrapper />);

        var meldinger = TestUtils.scryRenderedDOMComponentsWithClass(traadvisning, 'melding-container');
        expect(meldinger.length).to.equal(2);
    })
});