import FeilmeldingEnum from './../skriv/FeilmeldingEnum';

export const validateTextarea = (sporsmalInputtekst, harSubmittedSkjema) => {
    return !(sporsmalInputtekst.length === 0 && harSubmittedSkjema);
};

export const validateCheckbox = (godkjentVilkaar, harSubmittedSkjema) => {
    return !(!godkjentVilkaar && harSubmittedSkjema);
};

export const validate = (harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar) => {
    return getValidationMessages(harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar).length === 0;
};

export const getValidationMessages = (harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar) => {
    let validationMessages = [];
    if (!validateTextarea(sporsmalInputtekst, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.textarea);
    }
    if (!validateCheckbox(godkjentVilkaar, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.checkbox);
    }
    return validationMessages;
};