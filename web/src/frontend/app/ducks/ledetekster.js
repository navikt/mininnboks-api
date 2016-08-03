import * as Api from './../utils/api';
import { STATUS, doThenDispatch } from './utils';

// Actions
export const OK = 'mininnboks/ledetekster/OK';
export const FEILET = 'mininnboks/ledetekster/FEILET';
export const PENDING = 'mininnboks/ledetekster/PENDING';

const initalState = {
    status: STATUS.NOT_STARTED,
    godkjenteTemagrupper: [],
    data: {}
};


// Reducer
export default function reducer(state = initalState, action) {
    switch (action.type) {
        case PENDING:
            return { ...state, status: STATUS.PENDING };
        case FEILET:
            return { ...state, status: STATUS.ERROR, data: action.data };
        case OK: {
            const godkjenteTemagrupper = action.data['temagruppe.liste'].split(' ');
            return { ...state, status: STATUS.OK, data: action.data, godkjenteTemagrupper };
        }
        default:
            return state;
    }
}

// Action Creators
export function hentLedetekster() {
    return doThenDispatch(() => Api.hentLedetekster(), {
        OK,
        FEILET,
        PENDING
    });
}
