var React = require('react');
var TraadVisning = require('./TraadVisning');

var ListeVisning = React.createClass({
    getInitialState: function () {
        return {traader: []};
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/henvendelse/traader/').done(function (data) {
            this.setState(({
                traader: data
            }));
        }.bind(this));
    },
    render: function () {
        var traader = this.state.traader.map(function (traad) {
            return <TraadVisning traad={traad}/>;
        });
        return (
            <div>
                <h1 className="diger">Innboks</h1>
                <div className="top-bar">
                    <div className="innboks-navigasjon clearfix">
                        <a className="skriv-ny-link knapp-link-liten" href="https://www-t4.nav.no/no/NAV+og+samfunn/Kontakt+NAV/Kontakt+oss/skriv+til+oss/">Skriv ny melding</a>
                    </div>
                </div>
                <div className="traader-container">
            {traader}
                </div>
            </div>
        )
    }
});

module.exports = ListeVisning;