export const API_BASE_URL = '/saksoversikt/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
import {STATUS, doThenDispatch} from './utils';

// Actions
export const DOKUMENTVISNING_DATA_OK = 'DOKUMENTVISNING_DATA_OK';
export const DOKUMENTVISNING_DATA_FEILET = 'DOKUMENTVISNING_DATA_FEILET';
export const DOKUMENTVISNING_DATA_PENDING = 'DOKUMENTVISNING_DATA_PENDING';

const initalState = {
    status: STATUS.NOT_STARTED,
    data: {}
};

// Reducer
export default function reducer(state = initalState, action) {
    switch (action.type) {
        case DOKUMENTVISNING_DATA_PENDING:
            return { ...state, status: STATUS.PENDING };
        case DOKUMENTVISNING_DATA_FEILET:
            return { ...state, status: STATUS.ERROR };
        case DOKUMENTVISNING_DATA_OK:
            const [ dokumentmetadata, journalpostmetadata ] = action.data;
            return { ...state, status: STATUS.OK, data: { dokumentmetadata, journalpostmetadata }};
        default:
            return state;
    }
}

// ActionCreators
export const hentDokumentMetadata = (journalpostId, dokumentmetadata) =>
    fetch(`${API_BASE_URL}/dokumenter/dokumentmetadata/${journalpostId}/${dokumentmetadata}`,
        MED_CREDENTIALS)
        .then(res => res.json());

const hentJournalpostMetadata = (journalpostId) =>
    fetch(`${API_BASE_URL}/dokumenter/journalpostmetadata/${journalpostId}`, MED_CREDENTIALS)
        .then(res => res.json());

export const hentAlleDokumentMetadata = (journalpostId, dokumentreferanser) =>
    Promise.all(dokumentreferanser.split('-').map(dokumentId =>
        hentDokumentMetadata(journalpostId, dokumentId)));


export function hentDokumentVisningData(journalpostId, dokumentreferanser) {
    return doThenDispatch(() => Promise.all([
        hentAlleDokumentMetadata(journalpostId, dokumentreferanser),
        hentJournalpostMetadata(journalpostId)
    ]), {
        OK: DOKUMENTVISNING_DATA_OK,
        FEILET: DOKUMENTVISNING_DATA_FEILET,
        PENDING: DOKUMENTVISNING_DATA_PENDING
    });
}
