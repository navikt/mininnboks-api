import FeilmeldingEnum from '../skriv-nytt-sporsmal/feilmelding-enum';

export const validateTextarea = (fritekst, harSubmittedSkjema) => !(fritekst.length === 0 && harSubmittedSkjema);

export const validateCheckbox = (godkjentVilkaar, harSubmittedSkjema) => !(!godkjentVilkaar && harSubmittedSkjema);

export const getValidationMessages = (harSubmittedSkjema, fritekst, godkjentVilkaar) => {
    const validationMessages = [];
    if (!validateTextarea(fritekst, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.textarea);
    }
    if (!validateCheckbox(godkjentVilkaar, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.checkbox);
    }
    return validationMessages;
};

export const validate = (harSubmittedSkjema, fritekst, godkjentVilkaar) => (
    getValidationMessages(harSubmittedSkjema, fritekst, godkjentVilkaar).length === 0
);
