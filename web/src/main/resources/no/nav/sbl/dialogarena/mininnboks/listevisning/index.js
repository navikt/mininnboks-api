var React = require('react');
var TraaderContainer = require('./TraaderContainer');

var ListeVisning = React.createClass({
    render: function () {
        return (
            <div>
                <h1 className="diger">Innboks</h1>
                <div className="top-bar">
                    <div className="innboks-navigasjon clearfix">
                        <a className="skriv-ny-link knapp-hoved-liten" href="https://www-t4.nav.no/no/NAV+og+samfunn/Kontakt+NAV/Kontakt+oss/skriv+til+oss/">Skriv ny melding</a>
                    </div>
                </div>
                <TraaderContainer />
            </div>
        );
    }
});

module.exports = ListeVisning;