import ledetekster from '../ledetekster';
import { INIT_DATA } from '../init/initActions';
import initialState from '../init/initialState';
import { GODTA_VILKAAR, HENT_TRAADER, LES_TRAAD, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL, VIS_KVITTERING } from './../actions/actionTypes';

export default (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA: {
            const tekster = action.ledetekster;
            window.tekster = ledetekster(tekster);
            return Object.assign({}, state, {
                harHentetInitData: true,
                miljovariabler: action.miljovariabler,
                godkjentVilkaar: false,
                sporsmalInputtekst: '',
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
        case SKRIV_TEKST:
            return Object.assign({}, state, {sporsmalInputtekst: action.sporsmalInputtekst});
        case SUBMIT_SKJEMA:
            return Object.assign({}, state, {harSubmittedSkjema: true});
        case SETT_SENDING_STATUS:
            return Object.assign({}, state, {sendingStatus:  action.sendingStatus});
        case SKRIV_SVAR:
            return Object.assign({}, state, {skrivSvar:  action.skrivSvar});
        case LES_TRAAD:
            return Object.assign({}, state, {lesTraad:  action.lesTraad});
        default:
            return state;
    }
};
