import React, { PropTypes as pt } from 'react';
import Utils from '../utils/Utils';
import createFragment from 'react-addons-create-fragment';

class MeldingContainer extends React.Component {
    render () {
        const { melding } = this.props;
        const className = 'melding-container ' + (melding.fraBruker ? 'fra-bruker' : 'fra-nav');
        const imgSrc = melding.fraBruker ? '/mininnboks/build/img/personikon.svg' : '/mininnboks/build/img/nav-logo.svg';
        const imgTekstKey = melding.fraBruker ? 'innboks.avsender.bruker' : 'innboks.avsender.nav';

        const dato = Utils.prettyDate(melding.opprettet);
        const medUrl = function (innhold) {
            return Utils.leggTilLenkerTags(innhold);
        }.bind(this);

        let avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(medUrl)
            .map(Utils.tilAvsnitt(true));
        avsnitt = createFragment({
            avsnitt
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
}

MeldingContainer.propTypes = {
    resources: pt.shape({
        get: pt.func.isRequired
    }),
    melding: pt.shape({
        fraBruker: pt.bool,
        fritekst: pt.string,
        statusTekst: pt.string
    })
};

export default MeldingContainer;
