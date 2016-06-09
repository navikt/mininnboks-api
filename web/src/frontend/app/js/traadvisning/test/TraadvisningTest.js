/* eslint-env mocha */

import '../../testConfig';
import { expect, assert } from 'chai';
import sinon from 'sinon';
import React from 'react';
import stubRouterContext from '../../utils/StubRouterContext';
import Traadvisning from '../Traadvisning';

import { renderIntoDocument, scryRenderedDOMComponentsWithClass } from 'react-addons-test-utils';

const assign = Object.assign || require('object-assign');

const formatMessage = () => {};

const props = { formatMessage };

const meldinger = [{
    id: '1',
    traadId: '1',
    fritekst: '',
    kanal: null,
    eksternAktor: null,
    tilknyttetEnhet: null,
    temagruppeNavn: 'Familie',
    statusTekst: 'Spørsmål fra NAV – Familie',
    type: 'SPORSMAL_MODIA_UTGAAENDE',
    temagruppe: 'FMLI',
    opprettet: '2015-04-13T12:37:26.152Z',
    avsluttet: null,
    fraNav: true,
    fraBruker: false,
    kassert: false,
    lestDato: '2015-04-17T08:44:34.331Z',
    lest: true
}, {
    id: '2',
    traadId: '1',
    fritekst: '',
    kanal: null,
    eksternAktor: null,
    tilknyttetEnhet: null,
    temagruppeNavn: 'Familie',
    statusTekst: 'Spørsmål fra NAV – Familie',
    type: 'SPORSMAL_MODIA_UTGAAENDE',
    temagruppe: 'FMLI',
    opprettet: '2015-04-12T12:37:26.152Z',
    avsluttet: null,
    fraNav: true,
    fraBruker: false,
    kassert: false,
    lestDato: '2015-04-15T08:44:34.331Z',
    lest: true
}];

const traad = {
    traadId: '1',
    meldinger,
    nyeste: meldinger[0],
    kanBesvares: true,
    avsluttet: false
};

describe('Trådvisning initialisering', () => {
    before(() => {
        sinon.spy($, 'ajax');
    });

    after(() => {
        $.ajax.restore();
    });

    it('kan initialiseres med trådobjekt', () => {
        const TraadvisningWrapper = stubRouterContext(Traadvisning, assign({}, props, { valgtTraad: traad }));
        const traadvisning = renderIntoDocument(<TraadvisningWrapper />);

        assert.isTrue(traadvisning.getComponent().state.hentet);
        expect(traadvisning.getComponent().state.traad.traadId).to.equal('1');
    });

    it('kan initialiseres med trådid parameter', () => {
        const server = sinon.fakeServer.create();
        server.respondWith(/\/mininnboks\/tjenester\/traader\/(\d+)/, (xhr, traadId) => {
            expect(traadId).to.equal('1');
            xhr.respond(200, {}, traad);
        });

        const TraadvisningWrapper = stubRouterContext(Traadvisning, assign({}, props, { params: { traadId: '1' } }));
        renderIntoDocument(<TraadvisningWrapper />);

        server.restore();
    });
});

describe('Trådvisning', () => {
    it('viser tråd', () => {
        const TraadvisningWrapper = stubRouterContext(Traadvisning, assign({}, props, { valgtTraad: assign({}, traad) }));
        const traadvisning = renderIntoDocument(<TraadvisningWrapper />);

        const meldingerContainer = scryRenderedDOMComponentsWithClass(traadvisning, 'melding-container');
        expect(meldingerContainer.length).to.equal(2);
    });
});
