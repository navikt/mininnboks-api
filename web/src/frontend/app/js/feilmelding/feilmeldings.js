import React, { PropTypes as PT } from 'react';

function Feilmelding({ visIkon, melding, brodtekst }) {
    const ikon = visIkon ? <div className="robust-ikon-feil-gra"></div> : null;

    return (
        <section className="feilmelding">
            {ikon}
            <h1>
                {melding}
            </h1>
            <span>{brodtekst}</span>
        </section>
    );
}
Feilmelding.defaultProps = {
    visIkon: false
};
Feilmelding.propTypes = {
    visIkon: PT.bool,
    melding: PT.node.isRequired,
    brodtekst: PT.string.isRequired
};

export default Feilmelding;
