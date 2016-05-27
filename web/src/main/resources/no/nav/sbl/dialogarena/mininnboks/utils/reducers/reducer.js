import ledetekster from '../ledetekster';
import { INIT_DATA } from '../init/initActions';
import initialState from '../init/initialState';

const NORSK = "NORSK";

export default (state = initialState, action) => {
    switch (action.type) {
        case INIT_DATA:
        {
            const tekster = action.ledetekster;
            window.tekster = ledetekster(tekster);
            return Object.assign({}, state, {
                harHentetInitData: true,
                miljovariabler: action.miljovariabler,
                tekster
            });
        }
        default: 
            return state;
    }
};
