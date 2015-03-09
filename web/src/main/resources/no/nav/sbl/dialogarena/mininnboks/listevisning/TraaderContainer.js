var React = require('react');
var TraadPreview = require('./TraadPreview');
var TomInnboks = require('./TomInnboks');

module.exports = React.createClass({ getInitialState: function () {
    return {traader: [], hentet: false, feilet: false};
},
    componentDidMount: function () {
        $.get('/mininnboks/tjenester/traader/').done(function (data) {
            this.setState(({
                traader: data,
                hentet: true
            }));
        }.bind(this));
    },
    render: function() {
        if (!this.state.hentet) {
            return <div className="innboks-container snurrepipp"><img src='/mininnboks/img/ajaxloader/graa/loader_graa_128.gif' /></div>
        } else if (this.state.traader.length == 0) {
            return <div className="innboks-container"><TomInnboks /></div>
        } else {
            var traader = this.state.traader.map(function (traad) {
                return <TraadPreview traad={traad}/>;
            });
            return <div className="innboks-container">{traader}</div>;
        }
    }
});