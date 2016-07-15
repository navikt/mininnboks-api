import React, { PropTypes as pt } from 'react';
import { tilAvsnitt, prettyDate, leggTilLenkerTags } from '../utils/Utils';

class MeldingContainer extends React.Component {
    render () {
        const { melding, formatMessage } = this.props;
        const className = 'melding-container ' + (melding.fraBruker ? 'fra-bruker' : 'fra-nav');
        const imgSrc = melding.fraBruker ? '/mininnboks/build/img/person.svg' : '/mininnboks/build/img/nav-logo.svg';
        const imgTekstKey = melding.fraBruker ? 'innboks.avsender.bruker' : 'innboks.avsender.nav';

        const dato = prettyDate(melding.opprettet);

        const avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(leggTilLenkerTags)
            .map(tilAvsnitt);

        return (
            <div className={className}>
                <div className="logo" aria-label={formatMessage({ id: imgTekstKey })}>
                    <img src={imgSrc} alt={formatMessage({ id: imgTekstKey })} />
                </div>
                <div className="melding">
                    <h2 className="typo-element">{melding.statusTekst}</h2>
                    <p className="typo-infotekst tema-dokument">{dato}</p>
                    <div className="typo-normal">{avsnitt}</div>
                </div>
            </div>
        );
    }
}

MeldingContainer.propTypes = {
    melding: pt.shape({
        fraBruker: pt.bool,
        fritekst: pt.string,
        statusTekst: pt.string
    }),
    formatMessage: pt.func.isRequired
};

export default MeldingContainer;
