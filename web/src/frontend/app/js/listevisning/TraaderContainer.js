import React, { PropTypes as pt } from 'react';
import TraadPreview from './TraadPreview';
import MeldingListe from './MeldingListe';

function nyesteTraadForst(t1, t2) {
    const d1 = new Date(t1.nyeste.opprettet);
    const d2 = new Date(t2.nyeste.opprettet);

    if (d1 < d2) return 1;
    else if (d1 > d2) return -1;
    else return 0;
}


function getTraadLister(traader) {
    const sortert = traader.sort(nyesteTraadForst);
    const uleste = sortert.filter(traad => !traad.nyeste.lest);
    const leste = sortert.filter(traad => traad.nyeste.lest);

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
