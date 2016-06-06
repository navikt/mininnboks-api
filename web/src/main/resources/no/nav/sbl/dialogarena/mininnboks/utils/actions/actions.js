import { GODTA_VILKAAR, HENT_TRAADER, LES_TRAAD, RESET_STATE, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL } from './actionTypes';
import { getCookie } from '../Utils';

export const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
const SOM_POST = { credentials: 'same-origin', method: 'POST' };

export const velgGodtaVilkaar = (vilkaar) => ({ type: GODTA_VILKAAR, godkjentVilkaar: vilkaar });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const skrivTekst = (sporsmalInputtekst) => ({ type: SKRIV_TEKST, sporsmalInputtekst: sporsmalInputtekst });
export const submitSkjema = (harSubmittedSkjema) => ({ type: SUBMIT_SKJEMA, harSubmittedSkjema: harSubmittedSkjema });
export const settSendingStatus = (sendingStatus) => ({ type: SETT_SENDING_STATUS, sendingStatus: sendingStatus });
export const settSkrivSvar = (skrivSvar) => ({ type: SKRIV_SVAR, skrivSvar: skrivSvar });
export const resetInputState = () => ({ type: RESET_STATE, sporsmalInputtekst: '', sendingStatus: 'IKKE_SENDT', harSubmittedSkjema: false, godkjentVilkaar: false, skrivSvar: false });
export const hentTraader = () =>
    dispatch =>
        fetch(`${API_BASE_URL}/traader`, MED_CREDENTIALS)
            .then(res => res.json())
            .then(json => dispatch({
                type: HENT_TRAADER,
                traader: json
            }));

export const lesTraad = (traadId) => dispatch => fetch(`${API_BASE_URL}/traader/lest/${traadId}`, SOM_POST );
