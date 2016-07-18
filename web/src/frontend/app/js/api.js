const getCookie = (name) => {
    var re = new RegExp(name + '=([^;]+)');
    var match = re.exec(document.cookie);
    return match !== null ? match[1] : '';
};

const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
const SOM_POST = { credentials: 'same-origin', method: 'POST', headers: { 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS') }};

const SEND_SPORSMAL = (temagruppe, fritekst) => ({
    credentials: 'same-origin',
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS'),
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        temagruppe: temagruppe,
        fritekst: fritekst
    })
});

const SEND_SVAR = (traadId, fritekst) => ({
    credentials: 'same-origin',
    method: 'POST',
    headers: {
        'X-XSRF-TOKEN': getCookie('XSRF-TOKEN-MININNBOKS'),
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        traadId: traadId,
        fritekst: fritekst
    })
});

export const hentLedetekster = () => fetch(`${API_BASE_URL}/resources`, MED_CREDENTIALS).then(res => res.json());
export const hentTraader = () => fetch(`${API_BASE_URL}/traader`, MED_CREDENTIALS).then(res => res.json());

export const markerTraadSomLest = (traadId) => fetch(`${API_BASE_URL}/traader/allelest/${traadId}`, SOM_POST);
export const markerSomLest = (behandlingsId) => fetch(`${API_BASE_URL}/traader/lest/${behandlingsId}`, SOM_POST);

export const sendSporsmal = (temagruppe, fritekst) => fetch(`${API_BASE_URL}/traader/sporsmal`, SEND_SPORSMAL(temagruppe, fritekst));
export const sendSvar = (traadId, fritekst) => fetch(`${API_BASE_URL}/traader/svar`, SEND_SVAR(traadId, fritekst));