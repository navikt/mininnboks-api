import React, { PropTypes as PT } from 'react';
import { injectIntl } from 'react-intl';
import { tilAvsnitt, prettyDate, leggTilLenkerTags } from '../utils/utils';
import classNames from 'classnames';

const cls = (melding) => classNames('melding-container', {
    'fra-bruker': melding.fraBruker,
    'fra-nav': !melding.fraBruker
});

function MeldingContainer({ melding, intl: { formatMessage } }) {
    const imgSrc = melding.fraBruker ? '/mininnboks/img/person.svg' : '/mininnboks/img/nav-logo.svg';
    const imgAltTekst = formatMessage({ id: 'innboks.avsender' }, { fraBruker: melding.fraBruker });

    const dato = prettyDate(melding.opprettet);

    const avsnitt = melding.fritekst.split(/[\r\n]+/)
        .map(leggTilLenkerTags)
        .map(tilAvsnitt);

    return (
        <div className={cls(melding)}>
            <div className="logo">
                <img src={imgSrc} alt={imgAltTekst} />
            </div>
            <div className="melding">
                <h2 className="typo-element">{melding.statusTekst}</h2>
                <p className="typo-infotekst tema-dokument">{dato}</p>
                <div className="typo-normal">{avsnitt}</div>
            </div>
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
