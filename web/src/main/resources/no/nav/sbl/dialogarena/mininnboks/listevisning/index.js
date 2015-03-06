var React = require('react');
var TraadPreview = require('./TraadPreview');

var ListeVisning = React.createClass({
    getInitialState: function () {
        return {traader: []};
    },
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/traader/').done(function (data) {
            this.setState(({
                traader: data
            }));
        }.bind(this));
    },
    render: function () {
        var traader = this.state.traader.map(function (traad) {
            return <TraadPreview traad={traad}/>;
        });
        return (
            <div>
                <h1 className="diger">Innboks</h1>
                <div className="top-bar">
                    <div className="innboks-navigasjon clearfix">
                        <a className="skriv-ny-link knapp-hoved-liten" href="https://www-t4.nav.no/no/NAV+og+samfunn/Kontakt+NAV/Kontakt+oss/skriv+til+oss/">Skriv ny melding</a>
                    </div>
                </div>
                <div className="innboks-container">
                    {traader}
                </div>
            </div>
        )
    }
});

module.exports = ListeVisning;