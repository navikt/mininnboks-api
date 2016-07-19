import React from 'react';
import TraadPreview from './traad-preview';
import { FormattedMessage } from 'react-intl';

const MeldingListe = ({ meldinger, overskrift }) => {
    const innhold = meldinger.map((config) => (
        <TraadPreview
            aktiv={config.aktiv}
            key={config.traad.traadId}
            traad={config.traad}
            ulestMeldingKlasse={config.ulestMeldingKlasse}
        />
    ));

    return (
        <section className="traad-liste">
            <h1 className="panel blokk-xxxs clearfix typo-undertittel">
                <FormattedMessage id={overskrift} />
            </h1>
            <ul className="ustilet">
                {innhold}
            </ul>
        </section>
    );
};

export default MeldingListe;
