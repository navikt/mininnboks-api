var React = require('react');
var BesvarBoks = require('./BesvarBoks');
var MeldingContainer = require('./MeldingContainer');
var Knapper = require('./Knapper');

var TraadVisning = React.createClass({
    getInitialState: function () {
        return {
            meldinger: [],
            nyeste: {},
            kanBesvares: false,
            besvares: false
        };
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/traader/' + this.props.id).done(function (data) {
            this.setState(data);
        }.bind(this));
    },
    visBesvarBoks: function () {
        this.setState({besvares: true});
    },
    leggTilMelding: function (fritekst) {
        var meldinger = this.state.meldinger.splice(0);
        meldinger.unshift({
            fritekst: fritekst,
            opprettet: new Date(),
            fraBruker: true,
            fraNav: false,
            statusTekst: 'Svar om ' + meldinger[0].temagruppeNavn
        });
        this.setState({meldinger: meldinger, kanBesvares: false, besvares: false});
    },
    render: function () {
        var meldingItems = this.state.meldinger.map(function (melding) {
            return <MeldingContainer melding={melding} />
        });
        return (
            <div>
                <h1 className="diger">{this.state.nyeste.kassert ? 'Kassert dialog' : 'Dialog om ' + this.state.nyeste.temagruppeNavn}</h1>
                <div className="innboks-container traad-container">
                    <Knapper kanBesvares={this.state.kanBesvares} besvares={this.state.besvares} besvar={this.visBesvarBoks} />
                {this.state.besvares ? <BesvarBoks besvar={this.leggTilMelding} /> : null}
                {meldingItems}
                </div>
            </div>
        );
    }
});

module.exports = TraadVisning;