import { GODTA_VILKAAR, HENT_TRAADER, LES_TRAAD, RESET_STATE, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL } from './actionTypes';
import { getCookie } from '../Utils';

const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
const SOM_POST = { credentials: 'same-origin', method: 'POST', headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS') }};



export const velgGodtaVilkaar = (vilkaar) => ({ type: GODTA_VILKAAR, godkjentVilkaar: vilkaar });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const skrivTekst = (fritekst) => ({ type: SKRIV_TEKST, fritekst: fritekst });
export const submitSkjema = (harSubmittedSkjema) => ({ type: SUBMIT_SKJEMA, harSubmittedSkjema: harSubmittedSkjema });
export const settSendingStatus = (sendingStatus) => ({ type: SETT_SENDING_STATUS, sendingStatus: sendingStatus });
export const settSkrivSvar = (skrivSvar) => ({ type: SKRIV_SVAR, skrivSvar: skrivSvar });
export const resetInputState = () => ({ type: RESET_STATE, fritekst: '', sendingStatus: 'IKKE_SENDT', harSubmittedSkjema: false, godkjentVilkaar: false, skrivSvar: false });
export const hentTraader = () =>
    dispatch =>
        fetch(`${API_BASE_URL}/traader`, MED_CREDENTIALS)
            .then(res => res.json())
            .then(json => dispatch({
                type: HENT_TRAADER,
                traader: json
            }));

export const lesTraad = (traadId) => dispatch => fetch(`${API_BASE_URL}/traader/lest/${traadId}`, SOM_POST );

export const sendSporsmal = (temagruppe, fritekst) => dispatch => fetch(`${API_BASE_URL}/traader/sporsmal`, SEND_SPORSMAL(temagruppe, fritekst) );
export const sendSvar = (traadId, fritekst) => dispatch => fetch(`${API_BASE_URL}/traader/svar`, SEND_SVAR(traadId, fritekst) );

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

