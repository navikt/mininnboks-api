import React from 'react/addons';
import Utils from '../utils/Utils';

class MeldingContainer extends React.Component {
    render () {
        var melding = this.props.melding;
        var className = 'melding-container ' + (melding.fraBruker ? 'fra-bruker' : 'fra-nav');
        var imgSrc = melding.fraBruker ? '/mininnboks/build/img/personikon.svg' : '/mininnboks/build/img/nav-logo.svg';
        var imgTekstKey = melding.fraBruker ? 'innboks.avsender.bruker' : 'innboks.avsender.nav';

        var dato = Utils.prettyDate(melding.opprettet);
        var medUrl = function (innhold) {
            return Utils.leggTilLenkerTags(innhold);
        }.bind(this);

        var avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(medUrl)
            .map(Utils.tilAvsnitt(true));
        avsnitt = React.addons.createFragment({
            avsnitt: avsnitt
        });

        return (
            <div className={className}>
                <div className="logo" aria-label={this.props.resources.get(imgTekstKey)}>
                    <img src={imgSrc} alt={this.props.resources.get(imgTekstKey)} />
                </div>
                <div className="melding">
                    <h3 className="status">{melding.statusTekst}</h3>
                    <p className="dato">{dato}</p>
                    <div className="fritekst">{avsnitt}</div>
                </div>
            </div>
        );
    }
};

export default MeldingContainer;