import React from 'react';

const MeldingListe = ({ meldinger, formatMessage, overskrift }) => {
    const innhold = meldinger.length === 0 ? <noscript/> :
        (
            <ul className="ustilet">
                {meldinger}
            </ul>
        );

    return (
        <section className="traad-liste">
            <h1 className="panel blokk-xxxs clearfix typo-undertittel">
                {formatMessage({ id: overskrift })}
            </h1>
            {innhold}
        </section>
    );
};

export default MeldingListe;