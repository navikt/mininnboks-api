import React, { PropTypes as pt } from 'react';

class Knapper extends React.Component {
    constructor(props) {
        super(props);
        this.besvar = this.besvar.bind(this);
    }

    besvar(event) {
        event.preventDefault();
        if (this.props.kanBesvares) {
            this.props.besvar();
        }
    }

    render() {
        const { formatMessage, kanBesvares, besvares } = this.props;
        
        const skrivSvar = kanBesvares && !besvares ?
            <button onClick={this.besvar} className="knapp knapp-hoved knapp-liten">
                {formatMessage({ id: 'traadvisning.skriv.svar.link' })}
            </button> :
            <noscript/>;

        return (
            <div className="innboks-navigasjon">
                {skrivSvar}
            </div>
        );
    }
}

Knapper.propTypes = {
    formatMessage: pt.func.isRequired,
    kanBesvares: pt.bool,
    besvares: pt.bool,
    besvar: pt.func
};

export default Knapper;
