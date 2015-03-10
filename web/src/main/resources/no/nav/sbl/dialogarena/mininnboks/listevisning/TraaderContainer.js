var React = require('react');
var TraadPreview = require('./TraadPreview');
var TomInnboks = require('./TomInnboks');
var resources = require('resources');

module.exports = React.createClass({
    render: function () {
        if (this.props.traader.length == 0) {
            return <div className="innboks-container">
                <TomInnboks />
            </div>
        } else {
            var traader = this.props.traader.map(function (traad) {
                return <TraadPreview traad={traad}/>;
            });
            return <div className="innboks-container">{traader}</div>;
        }
    }
});