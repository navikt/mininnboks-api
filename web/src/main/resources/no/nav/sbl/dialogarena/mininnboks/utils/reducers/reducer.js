import ledetekster from '../ledetekster';
import { INIT_DATA } from '../init/initActions';
import initialState from '../init/initialState';
import { GODTA_VILKAAR, SKRIV_TEKST, VIS_MODAL } from './../actions/actionTypes';

export default (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA: {
            const tekster = action.ledetekster;
            window.tekster = ledetekster(tekster);
            return Object.assign({}, state, {
                harHentetInitData: true,
                miljovariabler: action.miljovariabler,
                godkjentVilkaar: false,
                sporsmal_inputtekst: '',
                tekster
            });
        }
        case GODTA_VILKAAR:
            return Object.assign({}, state, {godkjentVilkaar: action.godkjentVilkaar});
        case VIS_MODAL:
            return Object.assign({}, state, {visModal: action.visModal});
        case SKRIV_TEKST:
            return Object.assign({}, state, {sporsmal_inputtekst: action.sporsmal_inputtekst});
        default:
            return state;
    }
};
