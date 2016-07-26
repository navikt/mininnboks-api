import { reducer as formReducer } from 'redux-form';

export const resubmitAfterValid = (state, action) => {
    switch (action.type) {
        case 'redux-form/INITIALIZE':
            return { ...state, submitToken: null };
        case 'redux-form/SUBMIT_FAILED':
            return { ...state, submitToken: 'token' };
        case 'redux-form-plugin/FORM_VALID':
            return { ...state, submitToken: null };
        default:
            return state;
    }
};

export function reducer(...forms) {
    const config = forms.reduce((obj, formName) => ({
        ...obj,
        [formName]: resubmitAfterValid
    }), {});

    return formReducer.plugin(config);
}
