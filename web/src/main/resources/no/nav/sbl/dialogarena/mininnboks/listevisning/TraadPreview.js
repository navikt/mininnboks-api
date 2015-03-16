var React = require('react');
var AntallMeldinger = require('./AntallMeldinger');
var MeldingStatus = require('./MeldingStatus');
var Melding = require('melding');

var TraadPreview = React.createClass({
    markSomLest: function() {
        $.post('/mininnboks/tjenester/traader/lest/' + this.props.traad.nyeste.traadId);
    },
    render: function () {
        var melding = this.props.traad.nyeste;
        var className = 'traadlistevisning' + (melding.lest ? ' lest' : '');

        return (
            <a className={className} href={"/mininnboks/traad/" + melding.traadId} onClick={this.markSomLest}>
                <MeldingStatus  melding={melding} />
                <Melding melding={melding} />
                <AntallMeldinger antall={this.props.traad.meldinger.length} />
            </a>
        )
    }
});

module.exports = TraadPreview;