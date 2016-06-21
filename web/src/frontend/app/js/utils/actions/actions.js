import { GODTA_VILKAAR, HENT_TRAADER, TRAAD_LEST, RESET_STATE, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL } from './ActionTypes';
import { getCookie } from '../Utils';
import SendingStatus from '../../skriv/SendingStatus';

const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
const SOM_POST = { credentials: 'same-origin', method: 'POST', headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS') }};

export const dokumentVarselLest = (behandlingsId) => ({ type: 'DOKUMENT_VARSEL_LEST', behandlingsId });
export const velgGodtaVilkaar = (vilkaar) => ({ type: GODTA_VILKAAR, godkjentVilkaar: vilkaar });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const skrivTekst = (fritekst) => ({ type: SKRIV_TEKST, fritekst: fritekst });
export const submitSkjema = (harSubmittedSkjema) => ({ type: SUBMIT_SKJEMA, harSubmittedSkjema: harSubmittedSkjema });
export const settSendingStatus = (sendingStatus) => ({ type: SETT_SENDING_STATUS, sendingStatus: sendingStatus });
export const settSkrivSvar = (skrivSvar) => ({ type: SKRIV_SVAR, skrivSvar: skrivSvar });
export const traadLest = (traadId) => ({ type: TRAAD_LEST, traadId });
export const resetInputState = () => ({ type: RESET_STATE, fritekst: '', sendingStatus: 'IKKE_SENDT', harSubmittedSkjema: false, godkjentVilkaar: false, skrivSvar: false });
export const hentTraader = () =>
    dispatch =>
        hentTraaderFetch()
            .then(json => dispatch({
                type: HENT_TRAADER,
                traader: json
            }));

export const hentTraaderFetch = () =>
    fetch(`${API_BASE_URL}/traader`, MED_CREDENTIALS)
        .then(res => res.json());

export const lesTraad = (traadId) => dispatch =>
    fetch(`${API_BASE_URL}/traader/allelest/${traadId}`, SOM_POST)
        .then(() => dispatch(traadLest(traadId)));

export const lesDokumentVarsel = (behandlingsId) => dispatch =>
    fetch(`${API_BASE_URL}/traader/lest/${behandlingsId}`, SOM_POST)
        .then(() => dispatch(dokumentVarselLest(behandlingsId)));

export const sendSporsmal = (temagruppe, fritekst) => dispatch => fetch(`${API_BASE_URL}/traader/sporsmal`, SEND_SPORSMAL(temagruppe, fritekst))
    .then(() => dispatch(hentTraader()))
    .then(() => dispatch(settSendingStatus(SendingStatus.ok)))
    .catch((error) => dispatch(settSendingStatus(SendingStatus.feil)));

export const sendSvar = (traadId, fritekst) => dispatch => fetch(`${API_BASE_URL}/traader/svar`, SEND_SVAR(traadId, fritekst))
    .then(() => dispatch(hentTraader()))
    .then(() => dispatch(settSendingStatus(SendingStatus.ok)))
    .then(() => dispatch(resetInputState()))
    .catch((error) => dispatch(settSendingStatus(SendingStatus.feil)));

const SEND_SPORSMAL = (temagruppe, fritekst) => ({
    credentials: 'same-origin',
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS'),
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        temagruppe: temagruppe,
        fritekst: fritekst
    })
});

const SEND_SVAR = (traadId, fritekst) => ({
    credentials: 'same-origin',
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS'),
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        traadId: traadId,
        fritekst: fritekst
    })
});

