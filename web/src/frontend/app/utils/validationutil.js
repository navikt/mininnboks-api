export const validationRules = {
    fritekst: (verdi) => {
        if (!verdi || verdi.length === 0) {
            return 'required';
        }
        if (verdi && verdi.length > 1000) {
            return 'max-len';
        }
        return undefined;
    },
    godkjennVilkaar: (verdi) => {
        if (verdi !== true) {
            return 'required';
        }
        return undefined;
    }
};

export const validate = (verdier) => Object.entries(verdier).reduce((errors, [felt, verdi]) => {
    if (!validationRules.hasOwnProperty(felt)) {
        return errors;
    }

    const feltError = validationRules[felt](verdi);
    if (feltError) {
        return { ...errors, [felt]: feltError };
    }

    return errors;
}, {});
