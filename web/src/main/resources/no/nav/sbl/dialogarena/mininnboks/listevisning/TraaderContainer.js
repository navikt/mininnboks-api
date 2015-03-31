var React = require('react');
var TraadPreview = require('./TraadPreview');
var Feilmelding = require('../feilmelding/Feilmelding');
var resources = require('../resources/Resources');

var TraadContainer = React.createClass({
    render: function () {
        if (this.props.traader.length == 0) {
            return <div className="innboks-container">
                <Feilmelding melding='Her vil du motta svar på spørsmål du har sendt til NAV. Velg "Skriv til oss" for å sende inn spørsmål.'/>
            </div>
        } else {
            var traader = this.props.traader.map(function (traad) {
                return <TraadPreview key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad}/>;
            }.bind(this));
            return <div className="innboks-container">{traader}</div>;
        }
    }
});

module.exports = TraadContainer;