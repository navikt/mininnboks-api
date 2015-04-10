var React = require('react/addons');
var TraaderContainer = require('./TraaderContainer');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var Feilmelding = require('../feilmelding/Feilmelding');

var ListeVisning = React.createClass({
    getInitialState: function () {
        return {traader: [], hentet: false, feilet: {status: false}};
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/traader/').then(okCallback.bind(this), feiletCallback.bind(this));
    },
    render: function () {
        if (!this.state.hentet) {
            return <Snurrepipp />
        }

        var content;
        if (this.state.feilet.status) {
            content = <Feilmelding melding={this.state.feilet.melding} />;
        } else if (this.state.traader.length == 0) {
            content = <Feilmelding melding={this.props.resources.get('innboks.tom-innboks-melding')}/>;
        } else {
            content = <TraaderContainer traader={this.state.traader} setValgtTraad={this.props.setValgtTraad} resources={this.props.resources} />;
        }

        return (
            <div>
                <h1 className="diger">{this.props.resources.get('innboks.overskrift')}</h1>
                <div className="innboks-navigasjon clearfix">
                    <a className="skriv-ny-link knapp-hoved-liten" href={this.props.resources.get('skriv.ny.link')}>{this.props.resources.get('innboks.skriv.ny.link')}</a>
                </div>
                {content}
            </div>
        );
    }
});

function okCallback(data) {
    this.setState({
        traader: data,
        hentet: true
    });
}
function feiletCallback() {
    this.setState({
        feilet: {status: true, melding: this.props.resources.get('innboks.kunne-ikke-hente-meldinger')},
        hentet: true
    });
}

module.exports = ListeVisning;