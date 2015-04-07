var React = require('react/addons');
var TraadPreview = require('./TraadPreview');
var Feilmelding = require('../feilmelding/Feilmelding');

var TraadContainer = React.createClass({
    render: function () {
        if (this.props.traader.length == 0) {
            return <div className="innboks-container">
                <Feilmelding melding={this.props.resources.get('innboks.tom-innboks-melding')}/>
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