var React = require('react');
var Link = require('react-router').Link;
var AntallMeldinger = require('./AntallMeldinger');
var MeldingStatus = require('./MeldingStatus');
var Melding = require('../melding/Melding');

var TraadPreview = React.createClass({
    contextTypes: {
        router: React.PropTypes.func
    },
    onClick: function() {
        this.markerSomLest();
        this.context.router.valgtTraad = this.props.traad;
    },
    markerSomLest: function() {
        $.post('/mininnboks/tjenester/traader/lest/' + this.props.traad.nyeste.traadId);
    },
    render: function () {
        var melding = this.props.traad.nyeste;
        var className = 'traadlistevisning' + (melding.lest ? ' lest' : '');

        return (
            <Link to="traad" params={{traadId: melding.traadId}} className={className} onClick={this.onClick}>
                <AntallMeldinger antall={this.props.traad.meldinger.length} />
                <MeldingStatus  melding={melding} />
                <Melding melding={melding} />
            </Link>
        )
    }
});

module.exports = TraadPreview;