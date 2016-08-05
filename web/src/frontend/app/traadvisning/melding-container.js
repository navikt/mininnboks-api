import React, { PropTypes as PT } from 'react';
import { injectIntl } from 'react-intl';
import { tilAvsnitt, prettyDate, leggTilLenkerTags } from '../utils';
import { Relatertpanel } from 'nav-react-design/dist/panel';
import classNames from 'classnames';

const meldingklasse = (melding) => classNames('melding-container blokk-l', {
    'fra-bruker': melding.fraBruker,
    'fra-nav': !melding.fraBruker
});
const panelklasse = (melding) => classNames('melding', {
    'panel-relatert-venstre': !melding.fraBruker,
    'panel-relatert-hoyre': melding.fraBruker
});


function MeldingContainer({ melding, intl: { formatMessage } }) {
    const imgSrc = melding.fraBruker ? '/mininnboks/img/person.svg' : '/mininnboks/img/nav-logo.svg';
    const imgAltTekst = formatMessage({ id: 'innboks.avsender' }, { fraBruker: melding.fraBruker });

    const dato = prettyDate(melding.opprettet);

    const avsnitt = melding.fritekst.split(/[\r\n]+/)
        .map(leggTilLenkerTags)
        .map(tilAvsnitt);

    return (
        <div className={meldingklasse(melding)}>
            <div className="logo">
                <img src={imgSrc} alt={imgAltTekst} />
            </div>
            <Relatertpanel className={panelklasse(melding)}>
                <h2 className="typo-element">{melding.statusTekst}</h2>
                <p className="typo-infotekst tema-dokument">{dato}</p>
                <div className="typo-normal">{avsnitt}</div>
            </Relatertpanel>
        </div>
    );
}
MeldingContainer.propTypes = {
    melding: PT.shape({
        fraBruker: PT.bool,
        fritekst: PT.string,
        statusTekst: PT.string
    }),
    intl: PT.object.isRequired
};

export default injectIntl(MeldingContainer);
