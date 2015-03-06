var React = require('react');
var MeldingContainer = require('./MeldingContainer');
var Knapper = require('./Knapper');

var TraadVisning = React.createClass({
    getInitialState: function () {
        return {
            meldinger: [],
            kanBesvares: false
        };
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/traader/' + this.props.id).done(function (data) {
            this.setState(data);
        }.bind(this));
    },
    render: function () {
        var traad = this.state;
        console.log(traad);
        var meldingItems = traad.meldinger.map(function (melding) {
            return <MeldingContainer melding={melding} />
        });
        return (
            <div className="innboks-container traad-container">
                <Knapper kanBesvares={traad.kanBesvares} />
                {meldingItems}
            </div>
        );
    }
});

module.exports = TraadVisning;