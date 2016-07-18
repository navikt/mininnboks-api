import FeilmeldingEnum from '../skriv-nytt-sporsmal/FeilmeldingEnum';

export const validateTextarea = (fritekst, harSubmittedSkjema) => {
    return !(fritekst.length === 0 && harSubmittedSkjema);
};

export const validateCheckbox = (godkjentVilkaar, harSubmittedSkjema) => {
    return !(!godkjentVilkaar && harSubmittedSkjema);
};

export const validate = (harSubmittedSkjema, fritekst, godkjentVilkaar) => {
    return getValidationMessages(harSubmittedSkjema, fritekst, godkjentVilkaar).length === 0;
};

export const getValidationMessages = (harSubmittedSkjema, fritekst, godkjentVilkaar) => {
    let validationMessages = [];
    if (!validateTextarea(fritekst, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.textarea);
    }
    if (!validateCheckbox(godkjentVilkaar, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.checkbox);
    }
    return validationMessages;
};