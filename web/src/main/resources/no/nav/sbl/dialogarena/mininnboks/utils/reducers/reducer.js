import ledetekster from '../ledetekster';
import { INIT_DATA } from '../init/initActions';
import initialState from '../init/initialState';
import { GODTA_VILKAAR, VIS_MODAL } from './../actions/actionTypes';

export default (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA: {
            const tekster = action.ledetekster;
            window.tekster = ledetekster(tekster);
            return Object.assign({}, state, {
                harHentetInitData: true,
                miljovariabler: action.miljovariabler,
                godkjentVilkaar: false,
                tekster
            });
        }
        case GODTA_VILKAAR:
            return Object.assign({}, state, {godkjentVilkaar: action.godkjentVilkaar});
        case VIS_MODAL:
            return Object.assign({}, state, {visModal: action.visModal});
        default:
            return state;
    }
};
