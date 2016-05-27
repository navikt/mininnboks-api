import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import format from 'string-format';

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
        const skrivSvar = this.props.kanBesvares && !this.props.besvares ?
            <button onClick={this.besvar} className="knapp-hoved-liten">
                {this.props.resources.get('traadvisning.skriv.svar.link')}
            </button> :
            <noscript/>;

        return (
            <div className="knapper">
                {skrivSvar}
                <p>
                    <Link to="/mininnboks/" title="Tilbake til innboksen">{this.props.resources.get('traadvisning.innboks.link')}</Link>
                </p>
            </div>
        );
    }
}

Knapper.propTypes = {
    resources: pt.shape({
        get: pt.func.isRequired
    }),
    kanBesvares: pt.bool,
    besvares: pt.bool,
    besvar: pt.func
};

export default Knapper;
