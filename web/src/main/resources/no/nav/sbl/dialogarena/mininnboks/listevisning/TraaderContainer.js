var React = require('react/addons');
var TraadPreview = require('./TraadPreview');
var Feilmelding = require('../feilmelding/Feilmelding');

var TraadContainer = React.createClass({
    render: function () {
        var traader = this.props.traader.map(function (traad) {
            return <TraadPreview key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad}/>;
        }.bind(this));
        return <div>{traader}</div>;
    }
});

module.exports = TraadContainer;