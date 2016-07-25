import { combineReducers } from 'redux';
import { reducer as formReducer } from './../nav-form/nav-form-reducer';
import { INIT_DATA } from '../init/init-actions';
import initialState from '../init/initial-state';
import {
    HENT_TRAADER,
    LES_TRAAD,
    RESET_STATE,
    SETT_SENDING_STATUS,
    SKRIV_SVAR,
    VIS_MODAL,
    TRAAD_LEST
} from './../actions/action-types';
import { DOKUMENTVISNING_DATA } from '../../dokument-visning/dokument-actions';
import mapValues from 'lodash.mapvalues';

const dataReducer = (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA: {
            const { ledetekster, traader, options } = action;
            let tekster = ledetekster;
            if (options.cmskeys) {
                tekster = mapValues(tekster, (value, key) => `[${key}] ${value}`);
            }
            let godkjenteTemagrupper = ledetekster['temagruppe.liste'].split(' ');

            return { ...state,
                harHentetInitData: true,
                godkjenteTemagrupper,
                traader,
                tekster
            };
        }
        case HENT_TRAADER:
            return { ...state, traader: action.traader };
        case VIS_MODAL:
            return { ...state, visModal: action.visModal };
        case RESET_STATE:
            return {
                ...state,
                fritekst: '',
                sendingStatus: 'IKKE_SENDT',
                harSubmittedSkjema: false,
                godkjentVilkaar: false,
                skrivSvar: false
            };
        case SETT_SENDING_STATUS:
            return { ...state, sendingStatus: action.sendingStatus };
        case SKRIV_SVAR:
            return { ...state, skrivSvar: action.skrivSvar };
        case LES_TRAAD:
            return { ...state, lesTraad: action.lesTraad };
        case DOKUMENTVISNING_DATA:
            return { ...state, dokumentvisning: action.dokumentvisning };
        case TRAAD_LEST: {
            const markerSomLest = (melding) => Object.assign({}, melding, { lest: true });
            const traader = state.traader.map((traad) => {
                if (traad.traadId === action.traadId) {
                    const nyeste = markerSomLest(traad.nyeste);
                    const eldste = markerSomLest(traad.eldste);
                    const meldinger = traad.meldinger.map(markerSomLest);
                    return Object.assign({}, traad, { nyeste, eldste, meldinger });
                }
                return traad;
            });
            return Object.assign({}, state, { traader });
        }
        default:
            return state;
    }
};

export default combineReducers({
    data: dataReducer,
    form: formReducer('nytt-sporsmal')
})