import React, { PropTypes as PT } from 'react';

function Feilmelding({ visIkon, children }) {
    const ikon = visIkon ? <div className="robust-ikon-feil-gra"></div> : null;

    return (
        <section className="feilmelding">
            {ikon}
            {children}
        </section>
    );
}
Feilmelding.defaultProps = {
    visIkon: false
};
Feilmelding.propTypes = {
    visIkon: PT.bool,
    children: PT.node.isRequired
};

export default Feilmelding;
