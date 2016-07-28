import { hentLedetekster, hentTraader } from '../api';

export const INIT_DATA = 'INIT_DATA';

export const hentInitData = (options) => (dispatch) => Promise.all([hentLedetekster(), hentTraader()])
    .then(([ledetekster, traader]) =>
        dispatch({ type: INIT_DATA, ledetekster, options, traader })
    );
