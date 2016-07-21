import React, { PropTypes as PT } from 'react';
import MeldingPreview from './melding-preview';
import DokumentPreview from './dokument-preview';
import { FormattedMessage } from 'react-intl';

const MeldingListe = ({ meldinger, overskrift }) => {
    const innhold = meldinger.map((config) => {
        const type = config.traad.nyeste.type;
        const props = {
            aktiv: config.aktiv,
            key: config.traad.traadId,
            traad: config.traad,
            ulestMeldingKlasse: config.ulestMeldingKlasse
        };

        if (type === 'DOKUMENT_VARSEL') {
            return <DokumentPreview {...props} />;
        }
        return <MeldingPreview {...props} />;
    });

    return (
        <section className="traad-liste">
            <h1 className="panel blokk-xxxs clearfix typo-undertittel">
                <FormattedMessage id={overskrift} values={{ antallMeldinger: meldinger.length }} />
            </h1>
            <ul className="ustilet">
                {innhold}
            </ul>
        </section>
    );
};

MeldingListe.propTypes = {
    meldinger: PT.array.isRequired,
    overskrift: PT.string.siRequired
};

export default MeldingListe;
