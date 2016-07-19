import React, { PropTypes as PT } from 'react';
import { nyesteTraadForst } from './../utils/utils';
import TraadPreview from './traad-preview';
import MeldingListe from './melding-liste';

const getTraadLister = (traader) => {
    const sortert = traader.sort(nyesteTraadForst);
    const uleste = sortert.filter(traad => !traad.nyeste.lest);
    const leste = sortert.filter(traad => traad.nyeste.lest);

    return {
        uleste,
        leste
    };
}

const erAktivRegel = (fantVarselId, varselId) => {
    if (!fantVarselId) {
        return (_, index) => index === 0
    }
    return (melding) => melding.korrelasjonsId === varselId;
};

function TraadContainer({ traader, location }) {
    const traaderGruppert = getTraadLister(traader);

    const varselId = location.query.varselId;

    const fantVarselId = traader.find((traad) => traad.nyeste.korrelasjonsId === varselId);
    const erAktiv = erAktivRegel(fantVarselId, varselId);

    let ulesteTraader = traaderGruppert.uleste.map((traad, index) => (
        <TraadPreview aktiv={erAktiv(traad.nyeste, index)} key={traad.traadId} traad={traad} ulestMeldingKlasse="uleste-meldinger"/>
    ));
    let lesteTraader = traaderGruppert.leste.map((traad, index) => (
        <TraadPreview aktiv={erAktiv(traad.nyeste, index + ulesteTraader.length)} key={traad.traadId} traad={traad} />
    ));

    const ulesteMeldingerOverskrift = ulesteTraader.length === 0 ? 'innboks.uleste.ingenmeldinger' : 'innboks.uleste.tittel';
    const lesteMeldingerOverskrift = lesteTraader.length === 0 ? 'innboks.leste.ingenmeldinger' : 'innboks.leste.tittel';

    return (
        <div>
            <MeldingListe meldinger={ulesteTraader} overskrift={ulesteMeldingerOverskrift} />
            <MeldingListe meldinger={lesteTraader} overskrift={lesteMeldingerOverskrift} />
        </div>
    );
}

TraadContainer.propTypes = {
    traader: PT.array.isRequired
};

export default TraadContainer;
