import React, { PropTypes as pt } from 'react';

class Feilmelding extends React.Component {

    componentWillMount() {
        if (!this.props.melding) {
            throw "Feilmelding komponenten må ha en en definert props kalt 'melding'";
        }
    }

    render() {
        const { visIkon, melding, brodtekst } = this.props;
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
}

Feilmelding.propTypes = {
    visIkon: pt.bool,
    melding: pt.string,
    brodtekst: pt.string
};

export default Feilmelding;
