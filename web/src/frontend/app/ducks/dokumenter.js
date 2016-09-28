export const API_BASE_URL = '/saksoversikt/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
import { STATUS, fetchToJson, doThenDispatch } from './utils';

// Actions
export const DOKUMENTVISNING_DATA_OK = 'DOKUMENTVISNING_DATA_OK';
export const DOKUMENTVISNING_DATA_FEILET = 'DOKUMENTVISNING_DATA_FEILET';
export const DOKUMENTVISNING_DATA_PENDING = 'DOKUMENTVISNING_DATA_PENDING';
export const STATUS_PDF_MODAL = 'STATUS_PDF_MODAL';

const initalState = {
    status: STATUS.NOT_STARTED,
    data: {},
    pdfModal: {
        skalVises: false,
        dokumentUrl: null
    }
};

// Reducer
export default function reducer(state = initalState, action) {
    switch (action.type) {
        case DOKUMENTVISNING_DATA_PENDING:
            return { ...state, status: STATUS.PENDING };
        case DOKUMENTVISNING_DATA_FEILET:
            return { ...state, status: STATUS.ERROR };
        case DOKUMENTVISNING_DATA_OK: {
            const [dokumentmetadata, journalpostmetadata] = action.data;
            return { ...state, status: STATUS.OK, data: { dokumentmetadata, journalpostmetadata } };
        } case STATUS_PDF_MODAL:
            return { ...state, pdfModal: action.pdfModal };
        default:
            return state;
    }
}

// ActionCreators
const hentDokumentMetadata = (journalpostId, dokumentmetadata) =>
    fetchToJson(`${API_BASE_URL}/dokumenter/dokumentmetadata/${journalpostId}/${dokumentmetadata}`,
        MED_CREDENTIALS);

const hentJournalpostMetadata = (journalpostId) =>
    fetchToJson(`${API_BASE_URL}/dokumenter/journalpostmetadata/${journalpostId}`, MED_CREDENTIALS);

const hentAlleDokumentMetadata = (journalpostId, dokumentreferanser) =>
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

export function visLastNedPdfModal(dokumentUrl) {
    return { type: STATUS_PDF_MODAL, pdfModal: { skalVises: true, dokumentUrl } };
}

export function skjulLastNedPdfModal() {
    return { type: STATUS_PDF_MODAL, pdfModal: { skalVises: false, dokumentUrl: null } };
}

