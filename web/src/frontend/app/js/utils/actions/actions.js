import {
    HENT_TRAADER,
    TRAAD_LEST,
    RESET_STATE,
    SETT_SENDING_STATUS,
    SKRIV_SVAR,
    SUBMIT_SKJEMA,
    VIS_MODAL
} from './action-types';
import * as Api from './../../api';
import SendingStatus from '../../skriv-nytt-sporsmal/sending-status';
import { validate } from './../../validation/validationutil';

export const dokumentVarselLest = (behandlingsId) => ({ type: 'DOKUMENT_VARSEL_LEST', behandlingsId });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const submitSkjema = (harSubmittedSkjema) => ({ type: SUBMIT_SKJEMA, harSubmittedSkjema });
export const settSendingStatus = (sendingStatus) => ({ type: SETT_SENDING_STATUS, sendingStatus });
export const settSkrivSvar = (skrivSvar) => ({ type: SKRIV_SVAR, skrivSvar });
export const traadLest = (traadId) => ({ type: TRAAD_LEST, traadId });
export const revokeSubmittoken = ()=> ({ type: 'redux-form-plugin/FORM_VALID' });
export const resetInputState = () => ({ type: RESET_STATE });
export const hentTraader = () => (dispatch) => Api.hentTraader()
    .then(json => dispatch({
        type: HENT_TRAADER,
        traader: json
    }));


export const lesTraad = (traadId) => (dispatch) => Api.markerTraadSomLest(traadId)
    .then(() => dispatch(traadLest(traadId)));

export const lesDokumentVarsel = (behandlingsId) => (dispatch) => Api.markerSomLest(behandlingsId)
    .then(() => dispatch(dokumentVarselLest(behandlingsId)));

export const sendSporsmal = (temagruppe, fritekst) => (dispatch, getState) => {
    dispatch(submitSkjema(true));
    if (validate(true, fritekst, getState().godkjentVilkaar)) {
        Api.sendSporsmal(temagruppe, fritekst)
            .then(() => dispatch(hentTraader()))
            .then(() => dispatch(settSendingStatus(SendingStatus.ok)))
            .catch(() => dispatch(settSendingStatus(SendingStatus.feil)));
    }
};

export const sendSvar = (traadId, fritekst) => (dispatch) => {
    dispatch(submitSkjema(true));
    if (validate(true, fritekst, true)) {
        Api.sendSvar(traadId, fritekst)
            .then(() => dispatch(hentTraader()))
            .then(() => dispatch(settSendingStatus(SendingStatus.ok)))
            .then(() => dispatch(resetInputState()))
            .catch(() => dispatch(settSendingStatus(SendingStatus.feil)));
    }
};
