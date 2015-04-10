var React = require('react/addons');
var Link = require('react-router').Link;
var AntallMeldinger = require('./AntallMeldinger');
var MeldingStatus = require('./MeldingStatus');
var Melding = require('../melding/Melding');
var Utils = require('../utils/Utils');

var TraadPreview = React.createClass({
    onClick: function () {
        this.markerSomLest();
        this.props.setValgtTraad(this.props.traad);
    },
    markerSomLest: function () {
        $.post('/mininnboks/tjenester/traader/lest/' + this.props.traad.nyeste.traadId);
    },
    render: function () {
        var melding = this.props.traad.nyeste;
        var className = 'traadlistevisning' + (melding.lest ? ' lest' : '');
        var traadinfo = Utils.ariaLabelForMelding(this.props.traad.meldinger.length, melding);

        return (
            <Link to="traad" params={{traadId: melding.traadId}} className={className} onClick={this.onClick}>
                <AntallMeldinger antall={this.props.traad.meldinger.length} />
                <MeldingStatus  melding={melding} />
                <Melding melding={melding} traadinfo={traadinfo}/>
            </Link>
        )
    }
});

module.exports = TraadPreview;