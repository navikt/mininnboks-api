const API_BASE_URL = '/mininnboks/tjenester';
const MED_CREDENTIALS = { credentials: 'same-origin' };
export const INIT_DATA = 'INIT_DATA';

const hentLedetekster = () =>
    fetch(`${API_BASE_URL}/resources`, MED_CREDENTIALS).then(res => res.json());

export const hentInitData = (options) =>
    dispatch =>
        Promise.all([hentLedetekster()])
            .then(([ledetekster]) =>
                dispatch({ type: INIT_DATA, ledetekster, options }));
