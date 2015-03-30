var React = require('react');
var Link = require('react-router').Link;
var AntallMeldinger = require('./AntallMeldinger');
var MeldingStatus = require('./MeldingStatus');
var Melding = require('../melding/Melding');

var TraadPreview = React.createClass({
    markerSomLest: function() {
        $.post('/mininnboks/tjenester/traader/lest/' + this.props.traad.nyeste.traadId);
    },
    render: function () {
        var melding = this.props.traad.nyeste;
        var className = 'traadlistevisning' + (melding.lest ? ' lest' : '');

        return (
            <Link to="traad" params={{traadId: melding.traadId}} className={className} onClick={this.markerSomLest}>
                <AntallMeldinger antall={this.props.traad.meldinger.length} />
                <MeldingStatus  melding={melding} />
                <Melding melding={melding} />
            </Link>
        )
    }
});

module.exports = TraadPreview;