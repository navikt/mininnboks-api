var React = require('react');
var BesvarBoks = require('./BesvarBoks');
var MeldingContainer = require('./MeldingContainer');
var Knapper = require('./Knapper');
var Resources = require('../resources/Resources');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var Feilmelding = require('../feilmelding/Feilmelding');
var InfoBoks = require('../infoboks/Infoboks');
var format = require('string-format');
var Utils = require('../utils/Utils');

var TraadVisning = React.createClass({
    contextTypes: {
        router: React.PropTypes.func
    },
    getInitialState: function () {
        return {
            hentet: false,
            feilet: false,
            besvares: false,
            traad: {}
        };
    },
    componentDidMount: function () {
        var traaderDeferred = $.Deferred();
        Utils.whenFinished([traaderDeferred.promise(), Resources.promise]).then(okCallback.bind(this), feiletCallback.bind(this));
        if(this.context.router.valgtTraad) {
            traaderDeferred.resolve(this.context.router.valgtTraad);
        } else if (typeof this.props.params.traadId === 'string' && this.props.params.traadId.length > 0) {
            $.get('/mininnboks/tjenester/traader/' + this.props.params.traadId)
                .done(traaderDeferred.resolve.bind(traaderDeferred))
                .fail(traaderDeferred.reject.bind(traaderDeferred));
        } else {
            traaderDeferred.reject();
        }
    },
    visBesvarBoks: function () {
        this.setState({besvares: true});
    },
    skjulBesvarBoks: function () {
        this.setState({besvares: false});
    },
    sendMelding: function (fritekst) {
        $.ajax({
            type: 'POST',
            url: '/mininnboks/tjenester/traader/ny',
            contentType: 'application/json',
            data: JSON.stringify({traadId: this.state.traad.nyeste.traadId, fritekst: fritekst})
        }).done(leggTilMelding.bind(this, fritekst));
    },
    getInfoMelding: function () {
        if (this.state.traad.avsluttet) {
            return (
                <InfoBoks>
                    <p>
                        {Resources.get('traadvisning.kan-ikke-svare.info')}
                        {' '}
                        <a href={Resources.get('skriv.ny.link')}>{Resources.get('traadvisning.kan-ikke-svare.lenke')}</a>
                    </p>
                </InfoBoks>)
        } else if (!this.state.traad.avsluttet && !this.state.traad.kanBesvares) {
            var epost = Resources.get('bruker.epost');
            var infoTekst = epost ?
                Resources.get('traadvisning.send-svar.bekreftelse.du-mottar-epost') :
                Resources.get('traadvisning.send-svar.bekreftelse.kunne-ikke-hente-epost');
            var epostTekst = epost ?
                <span>
                    {epost}
                    {' '}
                    <a href={Resources.get('brukerprofil.link')}>{Resources.get('traadvisning.send-svar.bekreftelse.endre-epostadresse')}</a>
                </span> :
                <a href={Resources.get('brukerprofil.link')}>{Resources.get('traadvisning.send-svar.bekreftelse.registrer-epostadresse')}</a>;

            return (
                <InfoBoks>
                    <p>
                        {infoTekst} {epostTekst}
                    </p>
                </InfoBoks>)
        }

        return null;
    },
    render: function () {
        if (!this.state.hentet) {
            return <Snurrepipp />
        }
        if (this.state.feilet.status) {
            return (
                <div className="innboks-container">
                    <Feilmelding melding={this.state.feilet.melding} visIkon={true} />
                </div>
            );
        }

        var meldingItems = this.state.traad.meldinger.map(function (melding) {
            return <MeldingContainer key={melding.id} melding={melding} />
        });
        var overskrift = this.state.traad.nyeste.kassert ?
            Resources.get('traadvisning.overskrift.kassert') :
            format(Resources.get('traadvisning.overskrift'), this.state.traad.nyeste.temagruppeNavn);

        return (
            <div>
                <h1 className="diger">{overskrift}</h1>
                <div className="innboks-container traad-container">
                    <Knapper kanBesvares={this.state.traad.kanBesvares} besvares={this.state.besvares} besvar={this.visBesvarBoks} />
                    {this.getInfoMelding()}
                    <BesvarBoks besvar={this.sendMelding} vis={this.state.besvares} skjul={this.skjulBesvarBoks} />
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
}
function feiletCallback(data) {
    this.setState({
        feilet: {status: true, melding: Resources.get('traadvisning.feilmelding.hentet-ikke-traad') || 'Noe gikk galt'},
        hentet: true
    })
}

function leggTilMelding(fritekst) {
    var meldinger = this.state.traad.meldinger.splice(0);
    meldinger.unshift({
        fritekst: fritekst,
        opprettet: new Date(),
        temagruppeNavn: this.state.traad.nyeste.temagruppeNavn,
        fraBruker: true,
        fraNav: false,
        statusTekst: 'Svar om ' + meldinger[0].temagruppeNavn
    });
    this.setState({
        traad: {meldinger: meldinger, kanBesvares: false, nyeste: meldinger[0]},
        besvares: false
    });
}
module.exports = TraadVisning;