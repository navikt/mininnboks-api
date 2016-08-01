export const DOKUMENTVISNING_DATA = 'DOKUMENTVISNING_DATA';
export const API_BASE_URL = '/saksoversikt/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };

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

export const hentDokumentVisningData = (journalpostId, dokumentreferanser) =>
    dispatch =>
        Promise.all([
            hentAlleDokumentMetadata(journalpostId, dokumentreferanser),
            hentJournalpostMetadata(journalpostId)
        ]).then(([dokumentMetadataJson, journalpostMetadataJson]) => dispatch({
            type: DOKUMENTVISNING_DATA,
            dokumentvisning: {
                dokumentmetadata: dokumentMetadataJson,
                journalpostmetadata: journalpostMetadataJson.resultat,
                journalpostmetadataFeil: journalpostMetadataJson.feilendeSystemer
            }
        }));
