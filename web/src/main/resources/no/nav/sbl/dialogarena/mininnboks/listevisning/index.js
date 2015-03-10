var React = require('react');
var TraaderContainer = require('./TraaderContainer');
var resources = require('resources');

var ListeVisning = React.createClass({
    getInitialState: function () {
        return {traader: [], hentet: false, feilet: false};
    },
    componentDidMount: function () {
        var traaderPromise = $.get('/mininnboks/tjenester/traader/');
        $.when(traaderPromise, resources.promise).then(function (data) {
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
        }.bind(this));
    },
    render: function () {
        if (!this.state.hentet) {
            return <div className="innboks-container snurrepipp">
                <img src='/mininnboks/img/ajaxloader/graa/loader_graa_128.gif' />
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

module.exports = ListeVisning;