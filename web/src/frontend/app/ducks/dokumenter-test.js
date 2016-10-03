/* eslint-env mocha */
import reducer, * as E from './dokumenter';
import { STATUS } from './utils';
import { expect, Assertion } from 'chai';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import fetchMock from 'fetch-mock';

const dataAction = (type, data) => {
    if (data) {
        return { type, data };
    }
    return { type };
};
const middlewares = [thunk];
const mockStore = configureMockStore(middlewares);
const array = (value) => (Array.isArray(value) ? value : [value]);

Assertion.addMethod('received', function receivedMethod(actions, deepEquality = false) {
    const receivedActions = this._obj.getActions(); // eslint-disable-line no-underscore-dangle
    for (const action of array(actions)) {
        const received = receivedActions.find((receivedAction) => action.type === receivedAction.type);
        this.assert(
            received,
            'expected #{this} to have received #{exp}, but only found #{act}',
            'expected #{this} to dont care',
            action,
            JSON.stringify(receivedActions)
        );
        if (deepEquality) {
            new Assertion(received).to.deep.equal(action);
        } else if (action.data) {
            new Assertion(received.data).to.deep.equal(action.data);
        }
    }
});

describe('dokumenter-ducks', () => {
    describe('reducer', () => {
        it('skal oppdatere data-status korrekt', () => {
            const initalState = {};

            const pendingState = reducer(initalState, dataAction(E.DOKUMENTVISNING_DATA_PENDING));
            const feiletState = reducer(initalState, dataAction(E.DOKUMENTVISNING_DATA_FEILET));
            const okState = reducer(initalState, dataAction(E.DOKUMENTVISNING_DATA_OK, [0, 0]));

            expect(pendingState.status).to.equal(STATUS.PENDING);
            expect(feiletState.status).to.equal(STATUS.ERROR);
            expect(okState.status).to.equal(STATUS.OK);
        });

        it('skal oppdatere pdfModal korrekt', () => {
            const initalState = {};
            const dokumentUrl = 'http://vg.no';

            const visPdfModal = reducer(initalState, {
                type: E.STATUS_PDF_MODAL,
                pdfModal: {
                    skalVises: true,
                    dokumentUrl
                }
            });
            const skjulPdfModal = reducer(initalState, {
                type: E.STATUS_PDF_MODAL,
                pdfModal: {
                    skalVises: false,
                    dokumentUrl: null
                }
            });

            expect(visPdfModal).to.have.deep.property('pdfModal.skalVises', true);
            expect(visPdfModal).to.have.deep.property('pdfModal.dokumentUrl', dokumentUrl);

            expect(skjulPdfModal).to.have.deep.property('pdfModal.skalVises', false);
            expect(skjulPdfModal).to.have.deep.property('pdfModal.dokumentUrl', null);
        });
    });

    describe('actioncreators', () => {
        afterEach(fetchMock.restore);

        describe('hentDokumentVisningData', () => {
            it('Skal sende pending, og ok med korrekte data', () => {
                fetchMock.get('^/saksoversikt/tjenester/dokumenter/dokumentmetadata', { test: 'asda' });
                fetchMock.get('^/saksoversikt/tjenester/dokumenter/journalpostmetadata', { data: 'asda' });
                const store = mockStore({ status: STATUS.NOT_STARTED });

                return store.dispatch(E.hentDokumentVisningData('', ''))
                    .then(() => {
                        expect(store).to.have.received([
                            dataAction(E.DOKUMENTVISNING_DATA_PENDING, undefined),
                            dataAction(E.DOKUMENTVISNING_DATA_OK, [
                                [{ test: 'asda' }],
                                { data: 'asda' }
                            ])
                        ]);
                    });
            });

            it('skal sende pending, og error om dokumentmetadata feiler', (done) => {
                fetchMock.mock('^/saksoversikt/tjenester/dokumenter/dokumentmetadata', {
                    body: JSON.stringify({ test: 'asda' }),
                    status: 500
                });
                fetchMock.get('^/saksoversikt/tjenester/dokumenter/journalpostmetadata', { data: 'asda' });
                const store = mockStore({ status: STATUS.NOT_STARTED });

                const res = store.dispatch(E.hentDokumentVisningData('', ''));

                setTimeout(() => { // Må vente litt pga masse async/promise og dispatching.
                    res.then(() => {
                        expect(store).to.have.not.received(dataAction(E.DOKUMENTVISNING_DATA_OK));
                        expect(store).to.have.received([
                            dataAction(E.DOKUMENTVISNING_DATA_PENDING, undefined),
                            dataAction(E.DOKUMENTVISNING_DATA_FEILET)
                        ]);
                    });
                    done();
                }, 0);
            });

            it('skal sende pending, og error om dokumentmetadata feiler', (done) => {
                fetchMock.get('^/saksoversikt/tjenester/dokumenter/dokumentmetadata', { test: 'asda' });
                fetchMock.mock('^/saksoversikt/tjenester/dokumenter/journalpostmetadata', {
                    body: { data: 'asda' },
                    status: 500
                });
                const store = mockStore({ status: STATUS.NOT_STARTED });

                const res = store.dispatch(E.hentDokumentVisningData('', ''));

                setTimeout(() => { // Må vente litt pga masse async/promise og dispatching.
                    res.then(() => {
                        expect(store).to.have.not.received(dataAction(E.DOKUMENTVISNING_DATA_OK));
                        expect(store).to.have.received([
                            dataAction(E.DOKUMENTVISNING_DATA_PENDING, undefined),
                            dataAction(E.DOKUMENTVISNING_DATA_FEILET)
                        ]);
                    });
                    done();
                }, 0);
            });
        });

        it('visLastNedPdfModal', () => {
            const store = mockStore();
            const dokumentUrl = 'http://vg.no';

            store.dispatch(E.visLastNedPdfModal(dokumentUrl));

            expect(store).to.have.received(
                {
                    type: E.STATUS_PDF_MODAL,
                    pdfModal: {
                        skalVises: true,
                        dokumentUrl
                    }
                }, true); // Bruker ikke `data`-scopet så tvinger deep-equ-check
        });
        it('skjulLastNedPdfModal', () => {
            const store = mockStore();

            store.dispatch(E.skjulLastNedPdfModal());

            expect(store).to.have.received({
                type: E.STATUS_PDF_MODAL,
                pdfModal: {
                    skalVises: false,
                    dokumentUrl: null
                }
            }, true); // Bruker ikke `data`-scopet så tvinger deep-equ-check
        });
    });
});
