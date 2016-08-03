import { getCookie, fetchToJson } from './../ducks/utils';

const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
const somPostConfig = () => ({
    credentials: 'same-origin',
    method: 'POST',
    redirect: 'manual',
    headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS') }
});

const sendSporsmalConfig = (temagruppe, fritekst) => ({
    credentials: 'same-origin',
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS'),
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ temagruppe, fritekst })
});

const sendSvarConfig = (traadId, fritekst) => ({
    credentials: 'same-origin',
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS'),
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ traadId, fritekst })
});

export const hentLedetekster = () => fetchToJson(`${API_BASE_URL}/resources`, MED_CREDENTIALS);
export const hentTraader = () => fetchToJson(`${API_BASE_URL}/traader`, MED_CREDENTIALS);

export const markerTraadSomLest = (traadId) => fetchToJson(`${API_BASE_URL}/traader/allelest/${traadId}`, somPostConfig());
export const markerSomLest = (behandlingsId) => fetchToJson(`${API_BASE_URL}/traader/lest/${behandlingsId}`, somPostConfig());

export const sendSporsmal = (temagruppe, fritekst) => fetchToJson(
    `${API_BASE_URL}/traader/sporsmal`, sendSporsmalConfig(temagruppe, fritekst)
);
export const sendSvar = (traadId, fritekst) => fetchToJson(
    `${API_BASE_URL}/traader/svar`, sendSvarConfig(traadId, fritekst)
);
