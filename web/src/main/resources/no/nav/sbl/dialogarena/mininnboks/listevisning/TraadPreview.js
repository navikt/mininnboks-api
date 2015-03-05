var React = require('react');
var AntallMeldinger = require('./AntallMeldinger');
var MeldingStatus = require('./MeldingStatus');
var Melding = require('../innboks/Melding');

var TraadPreview = React.createClass({
    render: function () {
        var melding = this.props.traad.nyesteHenvendelse;
        var className = 'traadvisning' + (melding.lest ? ' lest' : '');

        return (
            <a className={className} href={"/mininnboks/traad/" + melding.traadId}>
                <MeldingStatus  melding={melding} />
                <Melding melding={melding} />
                <AntallMeldinger antall={this.props.traad.antallHenvendelser} />
            </a>
        )
    }
});

module.exports = TraadPreview;