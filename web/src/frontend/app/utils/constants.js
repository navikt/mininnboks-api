import keymirror from 'keymirror';

const Constants = keymirror({
    LEST: null,
    IKKE_LEST: null,
    BESVART: null,
    LEST_UBESVART: null
});

export const MeldingsTyper = {
    SPORSMAL_SKRIFTLIG: 'SPORSMAL_SKRIFTLIG',
    SVAR_SKRIFTLIG: 'SVAR_SKRIFTLIG',
    DELVIS_SVAR: 'DELVIS_SVAR_SKRIFTLIG'
};

export default Constants;
