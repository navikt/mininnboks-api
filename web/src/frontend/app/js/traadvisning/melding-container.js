import React, { PropTypes as PT } from 'react';
import { injectIntl } from 'react-intl';
import { tilAvsnitt, prettyDate, leggTilLenkerTags } from '../utils/Utils';

class MeldingContainer extends React.Component {
    render () {
        const { melding, intl: { formatMessage } } = this.props;
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
    melding: PT.shape({
        fraBruker: PT.bool,
        fritekst: PT.string,
        statusTekst: PT.string
    })
};

export default injectIntl(MeldingContainer);
