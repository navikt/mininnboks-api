var React = require('react/addons');
var BesvarBoks = require('./BesvarBoks');
var MeldingContainer = require('./MeldingContainer');
var Knapper = require('./Knapper');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var Feilmelding = require('../feilmelding/Feilmelding');
var InfoBoks = require('../infoboks/Infoboks');
var format = require('string-format');
var Utils = require('../utils/Utils');


var TraadVisning = React.createClass({
    getInitialState: function () {
        return {
            hentet: false,
            feilet: false,
            besvares: false,
            besvart: false,
            sendingfeilet: false,
            traad: {}
        };
    },
    componentDidMount: function () {
        if (this.props.valgtTraad) {
            okCallback.call(this, this.props.valgtTraad);
        } else if (typeof this.props.params.traadId === 'string' && this.props.params.traadId.length > 0) {
            $.get('/mininnboks/tjenester/traader/' + this.props.params.traadId)
                .then(okCallback.bind(this), feiletCallback.bind(this))
        } else {
            feiletCallback.call(this, this.props.valgtTraad);
        }
    },
    visBesvarBoks: function () {
        this.setState({besvares: true});
    },
    skjulBesvarBoks: function () {
        this.setState({besvares: false});
    },
    sendMelding: function (fritekst) {
        return $.ajax({
            type: 'POST',
            url: '/mininnboks/tjenester/traader/svar',
            contentType: 'application/json',
            data: JSON.stringify({traadId: this.state.traad.nyeste.traadId, fritekst: fritekst}),
            beforeSend: Utils.addXsrfHeader
        })
            .done(leggTilMelding.bind(this, fritekst))
            .fail(kunneIkkeLeggeTilMelding.bind(this));
    },
    getInfoMelding: function () {
        if (this.state.traad.avsluttet) {
            return (<InfoBoks.Info>
                <p>
                    {this.props.resources.get('traadvisning.kan-ikke-svare.info')}
                    {' '}
                    <a href={this.props.resources.get('skriv.ny.link')} className="lopendetekst">{this.props.resources.get('traadvisning.kan-ikke-svare.lenke')}</a>
                </p>
            </InfoBoks.Info>);
        } else if (this.state.besvart) {
            return (<InfoBoks.Ok focusOnRender={true}>
                <p>{this.props.resources.get('send-sporsmal.bekreftelse.varslingsinfo')}</p>
            </InfoBoks.Ok>);
        } else if (this.state.sendingfeilet) {
            return (<InfoBoks.Feil>
                <p>{this.props.resources.get('besvare.feilmelding.innsending')}</p>
            </InfoBoks.Feil>);
        }
        return null;
    },
    render: function () {
        if (!this.state.hentet) {
            return <Snurrepipp />
        }
        if (this.state.feilet.status) {
            return <Feilmelding melding={this.state.feilet.melding} visIkon={true} />;
        }

        var meldingItems = this.state.traad.meldinger.map(function (melding) {
            return <MeldingContainer key={melding.id} melding={melding} resources={this.props.resources}/>
        }.bind(this));

        var overskrift = this.state.traad.nyeste.kassert ?
            this.props.resources.get('traadvisning.overskrift.kassert') :
            format(this.props.resources.get('traadvisning.overskrift'), this.state.traad.nyeste.temagruppeNavn);

        return (
            <div>
                <h1 className="diger">{overskrift}</h1>
                <div className="traad-container">
                    <Knapper kanBesvares={this.state.traad.kanBesvares} besvares={this.state.besvares} besvar={this.visBesvarBoks} resources={this.props.resources} />
                    {this.getInfoMelding()}
                    <BesvarBoks besvar={this.sendMelding} vis={this.state.besvares} skjul={this.skjulBesvarBoks} resources={this.props.resources} />
                    {meldingItems}
                </div>
            </div>
        );
    }
});

function okCallback(data) {
    this.setState({
        traad: data,
        hentet: true
    });
    $.ajax({
        type: 'POST',
        url: '/mininnboks/tjenester/traader/lest/' + data.traadId,
        beforeSend: Utils.addXsrfHeader
    })
}
function feiletCallback() {
    this.setState({
        feilet: {status: true, melding: this.props.resources.get('traadvisning.feilmelding.hentet-ikke-traad')},
        hentet: true
    })
}

function leggTilMelding(fritekst, response, status, xhr) {
    if (xhr.status !== 201) {
        kunneIkkeLeggeTilMelding.call(this, response, status, xhr);
        return;
    }
    var meldinger = this.state.traad.meldinger.splice(0);
    meldinger.unshift({
        fritekst: fritekst,
        opprettet: new Date(),
        temagruppeNavn: this.state.traad.nyeste.temagruppeNavn,
        fraBruker: true,
        fraNav: false,
        statusTekst: this.props.resources.get('status.SVAR_SBL_INNGAAENDE').replace('%s', meldinger[0].temagruppeNavn)
    });

    this.setState({
        traad: {meldinger: meldinger, kanBesvares: false, nyeste: meldinger[0]},
        besvart: true,
        besvares: false
    });
}
function kunneIkkeLeggeTilMelding() {
    this.setState({
        sendingfeilet: true
    });
}
module.exports = TraadVisning;