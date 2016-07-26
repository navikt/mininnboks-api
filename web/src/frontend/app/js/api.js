import { getCookie } from './utils/utils';

const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
const SOM_POST = {
    credentials: 'same-origin',
    method: 'POST',
    headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS') }
};

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

export const safefetch = (...args) => fetch(...args).then((response) => {
    if (!response.ok) {
        throw new Error(response.statusText);
    }
    return response;
});

export const hentLedetekster = () => safefetch(`${API_BASE_URL}/resources`, MED_CREDENTIALS).then(res => res.json());
export const hentTraader = () => safefetch(`${API_BASE_URL}/traader`, MED_CREDENTIALS).then(res => res.json());

export const markerTraadSomLest = (traadId) => safefetch(`${API_BASE_URL}/traader/allelest/${traadId}`, SOM_POST);
export const markerSomLest = (behandlingsId) => safefetch(`${API_BASE_URL}/traader/lest/${behandlingsId}`, SOM_POST);

export const sendSporsmal = (temagruppe, fritekst) => safefetch(
    `${API_BASE_URL}/traader/sporsmal`, sendSporsmalConfig(temagruppe, fritekst)
);
export const sendSvar = (traadId, fritekst) => safefetch(
    `${API_BASE_URL}/traader/svar`, sendSvarConfig(traadId, fritekst)
);
