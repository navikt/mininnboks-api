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
        const traader = getTraadLister(this.props.traader);
        let ulesteTraader = traader.uleste.map((traad, index) => <TraadPreview index={index} key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad} resources={this.props.resources}/>);
        let lesteTraader = traader.leste.map(traad => <TraadPreview key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad} resources={this.props.resources}/>);

        if(lesteTraader.length === 0) {
            lesteTraader = <p className="panel">{this.props.resources.get('innboks.leste.ingenmeldinger')}</p>
        }

        if(ulesteTraader.length === 0) {
            ulesteTraader = <p className="panel">{this.props.resources.get('innboks.uleste.ingenmeldinger')}</p>
        }
        
        return (
            <div>
                <section className="ulest">
                    <h1 className="panel blokk-xxxs clearfix typo-undertittel">{this.props.resources.get('innboks.uleste.tittel')}</h1>
                    <ul className="ustilet">
                        {ulesteTraader}
                    </ul>
                </section>
                <section className="lest">
                    <h1 className="panel blokk-xxxs clearfix typo-undertittel">{this.props.resources.get('innboks.leste.tittel')}</h1>
                    <ul className="ustilet">
                        {lesteTraader}
                    </ul>
                </section>
            </div>
        );
    }
}

export default TraadContainer;
