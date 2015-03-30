var React = require('react');
var Link = require('react-router').Link;
var Resources = require('../resources/Resources');
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
            <button onClick={this.besvar} className="knapp-hoved-liten">{Resources.get('traadvisning.skriv.svar.link')}</button> :
            null;

        return (
            <div className="knapper">
                {skrivSvar}
                <Link to="innboks">{Resources.get('traadvisning.innboks.link')}</Link>
            </div>
        )
    }
});

module.exports = Knapper;