import { INNSENDING_OK } from './traader';

// Actions
export const VIS_VILKAR_MODAL = 'mininnboks/ui/VIS_VILKAR_MODAL';
export const VIS_BESVAR_BOKS = 'mininnboks/ui/VIS_BESVAR_BOKS';

const initalState = {
    visVilkarModal: false,
    visBesvarBoks: false
};


// Reducer
export default function reducer(state = initalState, action) {
    switch (action.type) {
        case VIS_VILKAR_MODAL:
            return { ...state, visVilkarModal: action.data };
        case VIS_BESVAR_BOKS:
            return { ...state, visBesvarBoks: action.data };
        case INNSENDING_OK:
            return { ...state, visBesvarBoks: false };
        default:
            return state;
    }
}

// Action Creators
export function visVilkarModal() {
    return { type: VIS_VILKAR_MODAL, data: true };
}
export function skjulVilkarModal() {
    return { type: VIS_VILKAR_MODAL, data: false };
}
export function visBesvarBoks() {
    return { type: VIS_BESVAR_BOKS, data: true };
}
export function skjulBesvarBoks() {
    return { type: VIS_BESVAR_BOKS, data: false };
}
