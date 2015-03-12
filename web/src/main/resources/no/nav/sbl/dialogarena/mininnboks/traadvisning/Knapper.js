var React = require('react');
var resources = require('resources');
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
            <button onClick={this.besvar} className="knapp-hoved-liten">{resources.get('traadvisning.skriv.svar.link')}</button> :
            null;

        return (
            <div className="knapper">
                {skrivSvar}
                <a href="/mininnboks">{resources.get('traadvisning.innboks.link')}</a>
            </div>
        )
    }
});

module.exports = Knapper;