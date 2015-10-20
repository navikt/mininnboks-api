var React = require('react/addons');
var Link = require('react-router').Link;
var format = require('string-format');

var Knapper = React.createClass({
    besvar: function (event) {
        event.preventDefault();
        if (this.props.kanBesvares) {
            this.props.besvar();
        }
    },
    render: function () {
        var skrivSvar = this.props.kanBesvares && !this.props.besvares ?
            <button onClick={this.besvar} className="knapp-hoved-liten">{this.props.resources.get('traadvisning.skriv.svar.link')}</button> :
            null;

        return (
            <div className="knapper">
                {skrivSvar}
                <Link to="innboks" title="Tilbake til innboksen" className="tilbake-til-innboks-link">{this.props.resources.get('traadvisning.innboks.link')}</Link>
            </div>
        )
    }
});

module.exports = Knapper;