import React from 'react';
import TraadPreview from './TraadPreview';

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
        const { formatMessage, setValgtTraad, query } = this.props;
        const traader = getTraadLister(this.props.traader);

        const varselId = "1"; //this.props.location.query.varselId;
        const matcherVarselId = this.props.traader.reduce((prev, curr) => prev || varselId && varselId === curr.nyeste.korrelasjonsId, false);
        const erAktiv = (melding, index) => !matcherVarselId && index === 0 || matcherVarselId && varselId === melding.korrelasjonsId ? true : false;

        let ulesteTraader = traader.uleste.map((traad, index) => <TraadPreview aktiv={erAktiv(traad.nyeste, index)} key={traad.traadId} traad={traad} setValgtTraad={setValgtTraad} formatMessage={formatMessage}/>);
        let lesteTraader = traader.leste.map((traad, index) => <TraadPreview aktiv={erAktiv(traad.nyeste, index + ulesteTraader.length)} key={traad.traadId} traad={traad} setValgtTraad={setValgtTraad} formatMessage={formatMessage}/>);

        if (lesteTraader.length === 0) {
            lesteTraader = <p className="panel">{formatMessage({ id: 'innboks.leste.ingenmeldinger' })}</p>;
        }

        if (ulesteTraader.length === 0) {
            ulesteTraader = <p className="panel">{formatMessage({ id: 'innboks.uleste.ingenmeldinger' })}</p>;
        }
        
        return (
            <div>
                <section className="ulest">
                    <h1 className="panel blokk-xxxs clearfix typo-undertittel">{formatMessage({ id: 'innboks.uleste.tittel' })}</h1>
                    <ul className="ustilet">
                        {ulesteTraader}
                    </ul>
                </section>
                <section className="lest">
                    <h1 className="panel blokk-xxxs clearfix typo-undertittel">{formatMessage({ id: 'innboks.leste.tittel' })}</h1>
                    <ul className="ustilet">
                        {lesteTraader}
                    </ul>
                </section>
            </div>
        );
    }
}

export default TraadContainer;
