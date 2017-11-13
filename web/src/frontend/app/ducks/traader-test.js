/* eslint-env mocha */
import reducer, * as E from './traader';
import { expect } from 'chai';
import { MeldingsTyper } from '../utils/constants';

function lagTraad(traadId, antallMeldinger) {
    const meldinger = new Array(antallMeldinger)
        .fill(0)
        .map((_, index) => ({
            id: `id${index + 1}`,
            traadId,
            lest: false
        }));

    return ({
        traadId,
        meldinger,
        nyeste: meldinger[0],
        eldste: meldinger[meldinger.length - 1]
    });
}

describe('traader-ducks', () => {
    describe('reducer', () => {
        it('skal oppdatere alle meldinger med med rett traadId med status lest', () => {
            const initialState = { data: [lagTraad('traad1', 2), lagTraad('traad2', 2)] };

            const markertSomLest = reducer(initialState, {
                type: E.MARKERT_SOM_LEST_OK,
                data: {
                    traadId: 'traad1'
                }
            });

            function harUlestMelding(meldinger) {
                const lestArray = meldinger.filter((melding) => (melding.lest));
                return lestArray[0] === undefined;
            }

            function erAlleMeldingerLest(traadId) {
                return markertSomLest.data
                    .filter((traad) => (traad.traadId === traadId))
                    .map((traad) => (traad.meldinger))
                    .filter(harUlestMelding);
            }

            expect(erAlleMeldingerLest('traad1')).to.deep.equal([]);
            expect(erAlleMeldingerLest('traad2')).to.not.deep.equal([]);
        });
    });
    describe('selector', () => {
        describe('traaderMedSammenslatteMeldinger', () => {
            const AVSLUTTENDE_SVAR_MELDINGS_ID = '3';
            const DELVIS_SVAR_TEKST = 'Jeg svarer på et delvis spørsmål';
            const SVAR_TEKST = 'Jeg avslutter det delvise spørsmålet ved å svare på oppgaven';
            const NYTT_SVAR_TEKST = 'Jeg stiller et helt nytt spørsmål';
            const initialState = {
                traader: {
                    data: [
                        {
                            meldinger: [
                                {
                                    id: '1',
                                    type: MeldingsTyper.SPORSMAL_SKRIFTLIG
                                },
                                {
                                    id: '2',
                                    type: MeldingsTyper.DELVIS_SVAR,
                                    fritekst: DELVIS_SVAR_TEKST
                                },
                                {
                                    id: AVSLUTTENDE_SVAR_MELDINGS_ID,
                                    type: MeldingsTyper.SVAR_SKRIFTLIG,
                                    fritekst: SVAR_TEKST
                                },
                                {
                                    id: '4',
                                    type: MeldingsTyper.SVAR_SKRIFTLIG,
                                    fritekst: NYTT_SVAR_TEKST
                                }
                            ]
                        }
                    ]
                }
            };
            it('skal ikke selecte delvise svar', () => {
                const sammenslaatteTraader = E.selectTraaderMedSammenslatteMeldinger(initialState);
                expect(sammenslaatteTraader.data[0].meldinger.length).to.equal(3);
            });
            it('skal merge teksten fra delvise svar inn i førstkommende skriftlige svar', () => {
                const sammenslaatteTraader = E.selectTraaderMedSammenslatteMeldinger(initialState);
                const avsluttendeSvar = sammenslaatteTraader.data[0].meldinger
                    .find(melding => melding.id === AVSLUTTENDE_SVAR_MELDINGS_ID);
                expect(avsluttendeSvar.fritekst).to.have.string(DELVIS_SVAR_TEKST)
                    .and.to.have.string(SVAR_TEKST);
            });
        });
    });
});

