import React, { PropTypes as PT } from 'react';
import Feilmelding from './../feilmelding/feilmelding';
import Laster from './innholdslaster-laster';
import { storeShape } from '../proptype-shapes';
import { STATUS } from './../ducks/utils';

const array = (value) => (Array.isArray(value) ? value : [value]);
const harStatus = (...status) => (element) => array(status).includes(element.status);
const noenHarFeil = (avhengigheter) => avhengigheter && avhengigheter.some(harStatus(STATUS.ERROR));
const alleLastet = (avhengigheter) => avhengigheter && avhengigheter.every(harStatus(STATUS.OK, STATUS.RELOADING));
const medFeil = (avhengigheter) => avhengigheter.find(harStatus(STATUS.ERROR));

const Innholdslaster = ({ avhengigheter, className, children }) => {
    if (alleLastet(avhengigheter)) {
        return <div className={className}>{children}</div>;
    }

    if (noenHarFeil(avhengigheter)) {
        const feilendeReducer = medFeil(avhengigheter);
        console.log(feilendeReducer);
        return (
            <Feilmelding tittel="Oops" className={className}>
                <p>Kunne ikke laste alle data som trengs for å vise applikasjonen</p>
            </Feilmelding>
        );
    }
    return <Laster className={className} />;
};

Innholdslaster.propTypes = {
    avhengigheter: PT.arrayOf(storeShape(PT.object)),
    className: PT.string,
    children: PT.node.isRequired
};

export default Innholdslaster;
