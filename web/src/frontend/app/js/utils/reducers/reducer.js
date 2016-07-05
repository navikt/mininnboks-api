import ledetekster from '../ledetekster';
import { INIT_DATA } from '../init/initActions';
import initialState from '../init/initialState';
import { GODTA_VILKAAR, HENT_TRAADER, LES_TRAAD, RESET_STATE, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL, TRAAD_LEST } from './../actions/actionTypes';
import mapValues from 'lodash.mapvalues';
import { DOKUMENTVISNING_DATA } from '../../dokumentvarsel/varsel-actions';

export default (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA: {
            let tekster = action.ledetekster;
            if (action.options.cmskeys) {
                tekster = mapValues(action.ledetekster, (value, key) => `[${key}] ${value}`);
            }
            window.tekster = ledetekster(tekster);
            return Object.assign({}, state, {
                harHentetInitData: true,
                traader: action.traader,
                miljovariabler: action.miljovariabler,
                godkjentVilkaar: false,
                fritekst: '',
                harSubmittedSkjema: false,
                sendingStatus: 'IKKE_SENDT',
                visKvittering: false,
                skrivSvar: false,
                tekster
            });
        }
        case GODTA_VILKAAR:
            return Object.assign({}, state, {godkjentVilkaar: action.godkjentVilkaar});
        case HENT_TRAADER:
            return Object.assign({}, state, {traader: action.traader});
        case VIS_MODAL:
            return Object.assign({}, state, {visModal: action.visModal});
        case RESET_STATE:
            return Object.assign({}, state, {godkjentVilkaar:  action.godkjentVilkaar, harSubmittedSkjema: action.harSubmittedSkjema, skrivSvar: action.skrivSvar, sendingStatus: action.sendingStatus, fritekst: action.fritekst});
        case SKRIV_TEKST:
            return Object.assign({}, state, {fritekst: action.fritekst});
        case SUBMIT_SKJEMA:
            return Object.assign({}, state, {harSubmittedSkjema: true});
        case SETT_SENDING_STATUS:
            return Object.assign({}, state, {sendingStatus:  action.sendingStatus});
        case SKRIV_SVAR:
            return Object.assign({}, state, {skrivSvar:  action.skrivSvar});
        case LES_TRAAD:
            return Object.assign({}, state, {lesTraad:  action.lesTraad});
        case DOKUMENTVISNING_DATA:
            return Object.assign({}, state, {dokumentvisning:  action.dokumentvisning});
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
