const validationRules = {
    fritekst: (verdi) => {
        if (!verdi || verdi.length === 0) {
            return 'required';
        }
        if (verdi && verdi.length > 1000) {
            return 'max-len';
        }
    },
    godkjennVilkaar: (verdi) => {
        if (verdi !== true) {
            return 'required';
        }
    }
};

export const validate = (verdier) => {
    return Object.entries(verdier).reduce((errors, [felt, verdi]) => {
        if (!validationRules.hasOwnProperty(felt)) {
            return errors;
        }

        const feltError = validationRules[felt](verdi);
        if (feltError) {
            errors[felt] = feltError;
        }

        return errors;
    }, {});
};
