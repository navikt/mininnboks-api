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
        const { formatMessage, setValgtTraad, location } = this.props;
        const traader = getTraadLister(this.props.traader);

        const varselId = location.query.varselId;
        const matcherVarselId = this.props.traader.reduce((prev, curr) => prev || varselId && varselId === curr.nyeste.korrelasjonsId, false);
        const erAktiv = (melding, index) => !matcherVarselId && index === 0 || matcherVarselId && varselId === melding.korrelasjonsId ? true : false;

        let ulesteTraader = traader.uleste.map((traad, index) => <TraadPreview aktiv={erAktiv(traad.nyeste, index)} key={traad.traadId} traad={traad} setValgtTraad={setValgtTraad} formatMessage={formatMessage}/>);
        let lesteTraader = traader.leste.map((traad, index) => <TraadPreview aktiv={erAktiv(traad.nyeste, index + ulesteTraader.length)} key={traad.traadId} traad={traad} setValgtTraad={setValgtTraad} formatMessage={formatMessage}/>);
        
        return (
            <div>
                { renderUleste(ulesteTraader, formatMessage) }
                { renderLeste(lesteTraader, formatMessage) }
            </div>
        );
    }
}

const renderUleste = (uleste, formatMessage) => {
    if (uleste.length === 0) {
        return (
            <section className="ulest">
                <h1 className="panel blokk-xxxs clearfix typo-undertittel">
                    {formatMessage({ id: 'innboks.uleste.ingenmeldinger' })}
                </h1>
            </section>
        );
    }
    return (
        <section className="ulest">
            <h1 className="panel blokk-xxxs clearfix typo-undertittel">{formatMessage({ id: 'innboks.uleste.tittel' })}</h1>
            <ul className="ustilet">
                {uleste}
            </ul>
        </section>
    );
};

const renderLeste = (leste, formatMessage) => {
    if (leste.length === 0) {
        return (
            <section className="lest">
                <h1 className="panel blokk-xxxs clearfix typo-undertittel">
                    {formatMessage({ id: 'innboks.leste.ingenmeldinger' })}
                </h1>
            </section>
        );
    }
    return (
        <section className="lest">
            <h1 className="panel blokk-xxxs clearfix typo-undertittel">{formatMessage({ id: 'innboks.leste.tittel' })}</h1>
            <ul className="ustilet">
                {leste}
            </ul>
        </section>
    );
};


export default TraadContainer;
