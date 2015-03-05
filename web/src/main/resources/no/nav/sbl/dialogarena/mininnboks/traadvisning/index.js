var React = require('react');
var MeldingContainer = require('./MeldingContainer');

var TraadVisning = React.createClass({
    getInitialState: function () {
        return {traad: []};
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/henvendelse/traad/' + this.props.id).done(function (data) {
            this.setState(({
                traad: data
            }));
        }.bind(this));
    },
    render: function () {
        console.log(this.state.traad);
        var meldingItems = this.state.traad.map(function (melding) {
            return <MeldingContainer melding={melding} />
        });
        return (
            <div className="innboks-container traad-container">
                {meldingItems}
            </div>
        );
    }
});

module.exports = TraadVisning;