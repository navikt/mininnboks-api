import { GODTA_VILKAAR, SETT_SENDING_STATUS, SKRIV_TEKST, SKRIV_SVAR, SUBMIT_SKJEMA, VIS_MODAL } from './actionTypes';

export const velgGodtaVilkaar = (vilkaar) => ({ type: GODTA_VILKAAR, godkjentVilkaar: vilkaar });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const skrivTekst = (sporsmalInputtekst) => ({ type: SKRIV_TEKST, sporsmalInputtekst: sporsmalInputtekst });
export const submitSkjema = (harSubmittedSkjema) => ({ type: SUBMIT_SKJEMA, harSubmittedSkjema: harSubmittedSkjema });
export const settSendingStatus = (sendingStatus) => ({ type: SETT_SENDING_STATUS, sendingStatus: sendingStatus });
export const settSkrivSvar = (skrivSvar) => ({ type: SKRIV_SVAR, skrivSvar: skrivSvar });