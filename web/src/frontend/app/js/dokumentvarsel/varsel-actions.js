export const DOKUMENTVISNING_DATA = 'DOKUMENTVISNING_DATA';
export const API_BASE_URL = 'https://A34DUVW25291.devillo.no:8587/saksoversikt/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };

export const hentDokumentMetadata = (journalpostId, dokumentmetadata, temakode) =>
    fetch(`${API_BASE_URL}/dokumenter/dokumentmetadata/${journalpostId}/${dokumentmetadata}/?temakode=${temakode}`,
        MED_CREDENTIALS)
        .then(res => res.json());

const hentJournalpostMetadata = (journalpostId) =>
    fetch(`${API_BASE_URL}/dokumenter/journalpostmetadata/${journalpostId}`, MED_CREDENTIALS)
        .then(res => res.json());


export const hentAlleDokumentMetadata = (journalpostId, dokumentreferanser, temakode) =>
    Promise.all(dokumentreferanser.split('-').map(dokumentId =>
        hentDokumentMetadata(journalpostId, dokumentId, temakode)));

export const hentDokumentVisningData = (journalpostId, dokumentreferanser, temakode) =>
    dispatch =>
        Promise.all([
            hentAlleDokumentMetadata(journalpostId, dokumentreferanser, temakode),
            hentJournalpostMetadata(journalpostId)
        ]).then(([dokumentMetadataJson, journalpostMetadataJson]) => dispatch({
            type: DOKUMENTVISNING_DATA,
            dokumentvisning: {
                dokumentmetadata: dokumentMetadataJson,
                journalpostmetadata: journalpostMetadataJson.resultat,
                journalpostmetadataFeil: journalpostMetadataJson.feilendeSystemer
            }
        }));

