import React from 'react';
import TraadPreview from './TraadPreview';
import Feilmelding from '../feilmelding/Feilmelding';

function getTraadLister (traader) {
    const uleste = traader.filter(traad => !traad.nyeste.lest);
    const leste = traader.filter(traad => traad.nyeste.lest);
    
    return {
        uleste,
        leste
    };
}

class TraadContainer extends React.Component {
    render () {
        const traader = getTraadLister(this.props.traader);
        
        let ulesteTraader = traader.uleste.map(traad => <TraadPreview key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad} resources={this.props.resources}/>);
        let lesteTraader = traader.leste.map(traad => <TraadPreview key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad} resources={this.props.resources}/>);

        if(lesteTraader.length === 0) {
            lesteTraader = <p className="panel"> Du har ingen leste meldinger</p>
        }

        if(ulesteTraader.length === 0) {
            ulesteTraader = <p className="panel"> Du har ingen uleste meldinger</p>
        }
        
        return (
            <div>
                <section className="ulest">
                    <h2 className="panel overstyrt blokk-xxxs clearfix">{this.props.resources.get('innboks.uleste.tittel')}</h2>
                    <ul className="ustilet">
                        {ulesteTraader}
                    </ul>
                </section>
                <section className="lest">
                    <h2 className="panel overstyrt blokk-xxxs clearfix lest">{this.props.resources.get('innboks.leste.tittel')}</h2>
                    <ul className="ustilet">
                        {lesteTraader}
                    </ul>
                </section>
            </div>);
    }
};

export default TraadContainer;