var React = require('react');
var TraaderContainer = require('./TraaderContainer');
var resources = require('resources');
var Snurrepipp = require('snurrepipp');
var Feilmelding = require('feilmelding');

var ListeVisning = React.createClass({
    getInitialState: function () {
        return {traader: [], hentet: false, feilet: {status: false}};
    },
    componentDidMount: function () {
        var traaderPromise = $.get('/mininnboks/tjenester/traader/');
        $.when(traaderPromise, resources.promise).then(okCallback.bind(this), feiletCallback.bind(this));
    },
    render: function () {
        if (!this.state.hentet) {
            return <Snurrepipp />
        } else if (this.state.feilet.status) {
            return <div className="innboks-container">
                <Feilmelding melding='Noe gikk feil' />
            </div>;
        } else {
            return (
                <div>
                    <h1 className="diger">{resources.get('innboks.overskrift')}</h1>
                    <div className="top-bar">
                        <div className="innboks-navigasjon clearfix">
                            <a className="skriv-ny-link knapp-hoved-liten" href="https://www-t4.nav.no/no/NAV+og+samfunn/Kontakt+NAV/Kontakt+oss/skriv+til+oss/">{resources.get('innboks.skriv.ny.link')}</a>
                        </div>
                    </div>
                    <TraaderContainer traader={this.state.traader} />
                </div>
            );
        }
    }
});

function okCallback(data) {
    if (data[1] === "success") {
        this.setState({
            traader: data[0],
            hentet: true
        });
    } else {
        this.setState({
            feilet: true,
            hentet: true
        })
    }
}
function feiletCallback(data) {
    this.setState({
        feilet: {status: true, melding: 'Kunne ikke hente ut dine meldinger.'},
        hentet: true
    });
}

module.exports = ListeVisning;