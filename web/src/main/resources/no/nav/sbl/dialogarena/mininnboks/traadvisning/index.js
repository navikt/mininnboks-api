var React = require('react');
var BesvarBoks = require('./BesvarBoks');
var MeldingContainer = require('./MeldingContainer');
var Knapper = require('./Knapper');

var TraadVisning = React.createClass({
    getInitialState: function () {
        return {
            meldinger: [],
            nyeste: {temagruppeNavn: ''},
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
    skjulBesvarBoks: function () {
        this.setState({besvares: false});
    },
    leggTilMelding: function (fritekst) {
        $.ajax({
            type: 'POST',
            url: '/mininnboks/tjenester/traader/ny',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({traadId: this.state.nyeste.traadId, fritekst: fritekst}),
            success: function() {
                var meldinger = this.state.meldinger.splice(0);
                meldinger.unshift({
                    fritekst: fritekst,
                    opprettet: new Date(),
                    fraBruker: true,
                    fraNav: false,
                    statusTekst: 'Svar om ' + meldinger[0].temagruppeNavn
                });
                this.setState({meldinger: meldinger, kanBesvares: false, besvares: false});
            }.bind(this)
        });
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
                    <BesvarBoks besvar={this.leggTilMelding} vis={this.state.besvares} skjul={this.skjulBesvarBoks} />
                    {meldingItems}
                </div>
            </div>
        );
    }
});

module.exports = TraadVisning;