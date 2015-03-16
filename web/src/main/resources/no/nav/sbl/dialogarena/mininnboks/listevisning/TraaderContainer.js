var React = require('react');
var TraadPreview = require('./TraadPreview');
var Feilmelding = require('feilmelding');
var resources = require('resources');

var TraadContainer = React.createClass({
    render: function () {
        if (this.props.traader.length == 0) {
            return <div className="innboks-container">
                <Feilmelding melding='Her vil du motta svar på spørsmål du har sendt til NAV. Velg "Skriv til oss" for å sende inn spørsmål.'/>
            </div>
        } else {
            var traader = this.props.traader.map(function (traad) {
                return <TraadPreview key={traad.traadId} traad={traad}/>;
            });
            return <div className="innboks-container">{traader}</div>;
        }
    }
});

module.exports = TraadContainer;