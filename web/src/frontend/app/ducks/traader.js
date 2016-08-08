import * as Api from './../utils/api';
import { STATUS, doThenDispatch } from './utils';

// Actions
export const HENT_ALLE_OK = 'mininnboks/traader/HENT_ALLE_OK';
export const HENT_ALLE_FEILET = 'mininnboks/traader/HENT_ALLE_FEILET';
export const HENT_ALLE_PENDING = 'mininnboks/traader/HENT_ALLE_PENDING';
export const HENT_ALLE_RELOAD = 'mininnboks/traader/HENT_ALLE_RELOAD';
export const MARKERT_SOM_LEST_OK = 'mininnboks/traader/MARKERT_SOM_LEST_OK';
export const MARKERT_SOM_LEST_FEILET = 'mininnboks/traader/MARKERT_SOM_LEST_FEILET';
export const INNSENDING_OK = 'mininnboks/traader/INNSENDING_OK';
export const INNSENDING_FEILET = 'mininnboks/traader/INNSENDING_FEILET';
export const INNSENDING_PENDING = 'mininnboks/traader/INNSENDING_PENDING';

const initalState = {
    status: STATUS.NOT_STARTED,
    innsendingStatus: STATUS.NOT_STARTED,
    data: []
};

// Utils
const markerMeldingSomLest = (melding) => ({ ...melding, lest: true });

// Reducer
export default function reducer(state = initalState, action) {
    switch (action.type) {
        case HENT_ALLE_PENDING:
            return { ...state, status: STATUS.PENDING };
        case HENT_ALLE_FEILET:
            return { ...state, status: STATUS.ERROR, data: action.data };
        case HENT_ALLE_OK:
            return { ...state, status: STATUS.OK, data: action.data };
        case HENT_ALLE_RELOAD:
            return { ...state, status: STATUS.RELOADING };
        case MARKERT_SOM_LEST_FEILET:
            return { ...state, status: STATUS.ERROR, data: action.data };
        case MARKERT_SOM_LEST_OK: {
            const traader = state.data.map((traad) => {
                if (traad.traadId === action.data.traadId) {
                    const nyeste = markerMeldingSomLest(traad.nyeste);
                    const eldste = markerMeldingSomLest(traad.eldste);
                    const meldinger = traad.meldinger.map(markerMeldingSomLest);
                    return { ...traad, nyeste, eldste, meldinger };
                }
                return traad;
            });
            return { ...state, data: traader, status: STATUS.OK };
        }
        case INNSENDING_OK:
            return { ...state, innsendingStatus: STATUS.OK };
        case INNSENDING_FEILET:
            return { ...state, innsendingStatus: STATUS.ERROR };
        case INNSENDING_PENDING:
            return { ...state, innsendingStatus: STATUS.PENDING };
        default:
            return state;
    }
}

const innsendingActions = {
    OK: INNSENDING_OK,
    FEILET: INNSENDING_FEILET,
    PENDING: INNSENDING_PENDING
};

// Action Creators
export function hentTraader(pendingType = HENT_ALLE_PENDING) {
    return doThenDispatch(() => Api.hentTraader(), {
        OK: HENT_ALLE_OK,
        FEILET: HENT_ALLE_FEILET,
        PENDING: pendingType
    });
}

export const sendSporsmal = (temagruppe, fritekst) => (dispatch) =>
    doThenDispatch(
        () => Api.sendSporsmal(temagruppe, fritekst).then(() => dispatch(hentTraader(HENT_ALLE_RELOAD))),
        innsendingActions
    )(dispatch);

export const sendSvar = (traadId, fritekst) => (dispatch) =>
    doThenDispatch(
        () => Api.sendSvar(traadId, fritekst).then(() => dispatch(hentTraader(HENT_ALLE_RELOAD))),
        innsendingActions
    )(dispatch);


export function markerTraadSomLest(traadId) {
    return doThenDispatch(() => Api.markerTraadSomLest(traadId), {
        OK: MARKERT_SOM_LEST_OK,
        FEILET: MARKERT_SOM_LEST_FEILET
    });
}
export function markerBehandlingsIdSomLest(behandlingsId) {
    return doThenDispatch(() => Api.markerSomLest(behandlingsId), {
        OK: MARKERT_SOM_LEST_OK,
        FEILET: MARKERT_SOM_LEST_FEILET
    });
}
