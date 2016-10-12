/* eslint-env mocha */
import reducer, * as E from './traader';
import { expect, Assertion } from 'chai';
import configureMockStore from 'redux-mock-store';
import { STATUS } from './utils'

describe('traader-ducks', () => {
    describe('reducer', () => {

        function erAlleMeldingerLest(traadId, markertSomLest) {
            return markertSomLest.data
                .filter((traad) => (traad.traadId === traadId))
                .map( (traad) => (traad.meldinger))
                .filter(harUlestMelding);
        }

        function harUlestMelding(meldinger) {
            const lestArray = meldinger.filter( (melding) => (melding.lest) );
            return lestArray[0] == undefined;
        }

        it('skal oppdatere alle meldinger med med rett traadId med status lest', () => {
            const messagesTraad1 = [
                {
                    id: 'id1',
                    traadId: 'traad1',
                    lest: false
                },
                {
                    id: 'id2',
                    traadId: 'traad1',
                    lest: false
                }];
            const messagesTraad2 = [
                {
                    id: 'id1',
                    traadId: 'traad2',
                    lest: false
                },
                {
                    id: 'id2',
                    traadId: 'traad2',
                    lest: false
                }];

            const initialState = {
                data: [{traadId: 'traad1',
                        meldinger: messagesTraad1,
                        nyeste: messagesTraad1[0],
                        eldste: messagesTraad1[1]
                        },
                    {traadId: 'traad2',
                        meldinger: messagesTraad2,
                        nyeste: messagesTraad2[0],
                        eldste: messagesTraad2[1]
                    }]
            };
            const markertSomLest = reducer(initialState, {
                type: E.MARKERT_SOM_LEST_OK,
                data: {
                    traadId: 'traad1'
                }
            });

            expect(erAlleMeldingerLest('traad1', markertSomLest)).to.deep.equal([]);
            expect(erAlleMeldingerLest('traad2',markertSomLest)).to.not.deep.equal([]);
        });
    });
});


