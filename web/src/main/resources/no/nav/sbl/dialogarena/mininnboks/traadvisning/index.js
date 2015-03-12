var React = require('react');
var BesvarBoks = require('./BesvarBoks');
var MeldingContainer = require('./MeldingContainer');
var Knapper = require('./Knapper');
var resources = require('resources');
var Snurrepipp = require('../innboks/Snurrepipp');
var format = require('string-format');


var TraadVisning = React.createClass({
    getInitialState: function () {
        return {
            hentet: false,
            feilet: false,
            besvares: false,
            traad: {}
        };
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/traader/' + this.props.id).done(function (data) {
            this.setState(data);
        }.bind(this));
        var traadPromise = $.get('/mininnboks/tjenester/traader/' + this.props.id);
        $.when(traadPromise, resources.promise).then(function (data) {
            if (data[1] === "success") {
                this.setState({
                    traad: data[0],
                    hentet: true
                });
            } else {
                this.setState({
                    hentet: true,
                    feilet: true
                })
            }
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
            data: JSON.stringify({traadId: this.state.traad.nyeste.traadId, fritekst: fritekst})
        }).done(function () {
            var meldinger = this.state.meldinger.splice(0);
            meldinger.unshift({
                fritekst: fritekst,
                opprettet: new Date(),
                fraBruker: true,
                fraNav: false,
                statusTekst: 'Svar om ' + meldinger[0].temagruppeNavn
            });
            this.setState({
                traad: {meldinger: meldinger, kanBesvares: false, nyeste: meldinger[0]},
                besvares: false
            });
        }.bind(this));
    },
    render: function () {
        if (!this.state.hentet) {
            return <Snurrepipp />
        } else {
            var meldingItems = this.state.traad.meldinger.map(function (melding) {
                return <MeldingContainer melding={melding} />
            });
            var overskrift = this.state.traad.nyeste.kassert ? resources.get('traadvisning.overskrift.kassert') : format(resources.get('traadvisning.overskrift'), this.state.traad.nyeste.temagruppeNavn);
            return (
                <div>
                    <h1 className="diger">{overskrift}</h1>
                    <div className="innboks-container traad-container">
                        <Knapper avsluttet={this.state.traad.avsluttet} kanBesvares={this.state.traad.kanBesvares} besvares={this.state.besvares} besvar={this.visBesvarBoks} />
                        <BesvarBoks besvar={this.leggTilMelding} vis={this.state.besvares} skjul={this.skjulBesvarBoks} />
                    {meldingItems}
                    </div>
                </div>
            );
        }
    }
});

module.exports = TraadVisning;