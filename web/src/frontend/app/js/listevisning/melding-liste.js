import React from 'react';
import { FormattedMessage } from 'react-intl';

const MeldingListe = ({ meldinger, overskrift }) => {
    const innhold = meldinger.length === 0 ? null : (
        <ul className="ustilet">
            {meldinger}
        </ul>
    );

    return (
        <section className="traad-liste">
            <h1 className="panel blokk-xxxs clearfix typo-undertittel">
                <FormattedMessage id={overskrift} />
            </h1>
            {innhold}
        </section>
    );
};

export default MeldingListe;
