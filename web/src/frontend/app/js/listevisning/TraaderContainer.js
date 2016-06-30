import React, { PropTypes as pt } from 'react';
import TraadPreview from './TraadPreview';
import MeldingListe from './MeldingListe';

function getTraadLister(traader) {
    const uleste = traader.filter(traad => !traad.nyeste.lest);
    const leste = traader.filter(traad => traad.nyeste.lest);

    return {
        uleste,
        leste
    };
}

class TraadContainer extends React.Component {
    render() {
        const { formatMessage, setValgtTraad, location } = this.props;
        const traader = getTraadLister(this.props.traader);

        const varselId = location.query.varselId;
        const matcherVarselId = this.props.traader.reduce((prev, curr) => prev || varselId && varselId === curr.nyeste.korrelasjonsId, false);
        const erAktiv = (melding, index) => !matcherVarselId && index === 0 || matcherVarselId && varselId === melding.korrelasjonsId ? true : false;

        let ulesteTraader = traader.uleste.map((traad, index) => <TraadPreview aktiv={erAktiv(traad.nyeste, index)} key={traad.traadId} traad={traad} setValgtTraad={setValgtTraad} formatMessage={formatMessage} ulestMeldingKlasse="uleste-meldinger"/>);
        let lesteTraader = traader.leste.map((traad, index) => <TraadPreview aktiv={erAktiv(traad.nyeste, index + ulesteTraader.length)} key={traad.traadId} traad={traad} setValgtTraad={setValgtTraad} formatMessage={formatMessage}/>);

        const ulesteMeldingerOverskrift = ulesteTraader.length === 0 ? 'innboks.uleste.ingenmeldinger' : 'innboks.uleste.tittel';
        const lesteMeldingerOverskrift = lesteTraader.length === 0 ? 'innboks.leste.ingenmeldinger' : 'innboks.leste.tittel';

        return (
            <div>
                <MeldingListe meldinger={ulesteTraader} formatMessage={formatMessage} overskrift={ulesteMeldingerOverskrift} />
                <MeldingListe meldinger={lesteTraader} formatMessage={formatMessage} overskrift={lesteMeldingerOverskrift} />
            </div>
        );
    }
}

TraadContainer.propTypes = {
    formatMessage: pt.func.isRequired,
    setValgtTraad: pt.func,
    traader: pt.array
};

export default TraadContainer;
