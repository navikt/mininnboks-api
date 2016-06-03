import { GODTA_VILKAAR, SKRIV_TEKST, VIS_MODAL } from './actionTypes';

export const velgGodtaVilkaar = (vilkaar) => ({ type: GODTA_VILKAAR, godkjentVilkaar: vilkaar });
export const velgVisModal = (skalVise) => ({ type: VIS_MODAL, visModal: skalVise });
export const skrivTekst = (sporsmal_inputtekst) => ({ type: SKRIV_TEKST, sporsmal_inputtekst: sporsmal_inputtekst });