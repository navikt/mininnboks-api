import React from 'react/addons';
import { Link } from 'react-router';
import format from 'string-format';

var Knapper = React.createClass({
    besvar: function (event) {
        event.preventDefault();
        if (this.props.kanBesvares) {
            this.props.besvar();
        }
    },
    render: function () {
        var skrivSvar = this.props.kanBesvares && !this.props.besvares ?
            <button onClick={this.besvar}
                    className="knapp-hoved-liten">{this.props.resources.get('traadvisning.skriv.svar.link')}</button> :
            null;

        return (
            <div className="knapper">
                {skrivSvar}
                <p>
                    <Link to="/mininnboks/" title="Tilbake til innboksen">{this.props.resources.get('traadvisning.innboks.link')}</Link>
                </p>
            </div>
        )
    }
});

module.exports = Knapper;