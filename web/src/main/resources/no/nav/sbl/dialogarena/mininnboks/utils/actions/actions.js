import { GODTA_VILKAAR, HENT_TRAADER, LES_TRAAD, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL } from './actionTypes';

export const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };

export const velgGodtaVilkaar = (vilkaar) => ({ type: GODTA_VILKAAR, godkjentVilkaar: vilkaar });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const skrivTekst = (sporsmalInputtekst) => ({ type: SKRIV_TEKST, sporsmalInputtekst: sporsmalInputtekst });
export const submitSkjema = (harSubmittedSkjema) => ({ type: SUBMIT_SKJEMA, harSubmittedSkjema: harSubmittedSkjema });
export const settSendingStatus = (sendingStatus) => ({ type: SETT_SENDING_STATUS, sendingStatus: sendingStatus });
export const settSkrivSvar = (skrivSvar) => ({ type: SKRIV_SVAR, skrivSvar: skrivSvar });
export const hentTraader = () =>
    dispatch =>
        fetch(`${API_BASE_URL}/traader`, MED_CREDENTIALS)
            .then(res => res.json())
            .then(json => dispatch({
                type: HENT_TRAADER,
                traader: json
            }));
export const lesTraad = (traadId) =>
    dispatch =>
        fetch(`${API_BASE_URL}/traader/lest/` + traadId, MED_CREDENTIALS)
            .then(res => res.json())
            .then(json => dispatch({
                type: LES_TRAAD,
                traader: json
            }));