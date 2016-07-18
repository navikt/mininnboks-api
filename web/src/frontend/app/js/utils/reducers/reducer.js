import { INIT_DATA } from '../init/initActions';
import initialState from '../init/initialState';
import { GODTA_VILKAAR, HENT_TRAADER, LES_TRAAD, RESET_STATE, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL, TRAAD_LEST } from './../actions/actionTypes';
import { DOKUMENTVISNING_DATA } from '../../dokumentvarsel/varsel-actions';
import mapValues from 'lodash.mapvalues';

export default (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA: {
            const { ledetekster, traader, miljovariabler, options } = action;
            let tekster = ledetekster;
            if (options.cmskeys) {
                tekster = mapValues(tekster, (value, key) => `[${key}] ${value}`);
            }
            
            return {...state,
                harHentetInitData: true,
                traader,
                miljovariabler,
                tekster
            };
        }
        case GODTA_VILKAAR:
            return { ...state, godkjentVilkaar: action.godkjentVilkaar };
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
        case SKRIV_TEKST:
            return { ...state, fritekst: action.fritekst };
        case SUBMIT_SKJEMA:
            return { ...state, harSubmittedSkjema: true };
        case SETT_SENDING_STATUS:
            return { ...state, sendingStatus:  action.sendingStatus };
        case SKRIV_SVAR:
            return { ...state, skrivSvar:  action.skrivSvar };
        case LES_TRAAD:
            return { ...state, lesTraad:  action.lesTraad };
        case DOKUMENTVISNING_DATA:
            return { ...state, dokumentvisning:  action.dokumentvisning };
        case TRAAD_LEST: {
            const markerSomLest = (melding) => Object.assign({}, melding, { lest: true });
            const traader = state.traader.map((traad) => {
                if(traad.traadId === action.traadId) {
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
