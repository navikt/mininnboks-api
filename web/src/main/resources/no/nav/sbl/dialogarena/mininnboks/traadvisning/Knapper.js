import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';

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
            <button onClick={this.besvar} className="knapp-hoved-liten">
                {formatMessage({ id: 'traadvisning.skriv.svar.link' })}
            </button> :
            <noscript/>;

        return (
            <div className="knapper">
                {skrivSvar}
                <p>
                    <Link to="/mininnboks/" title="Tilbake til innboksen">{formatMessage({ id: 'traadvisning.innboks.link' })}</Link>
                </p>
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
