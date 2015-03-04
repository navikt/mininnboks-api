var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');
var AntallMeldinger = require('./AntallMeldinger');

var TraadVisning = React.createClass({
    render: function () {
        var melding = this.props.traad.nyesteHenvendelse;
        var dato = moment(melding.opprettet.millis).format('Do MMMM YYYY, [kl.] HH:mm');

        var lestImg = !melding.lest ? <img src="/mininnboks/img/melding_ny.svg" className="ikon melding-info" /> : null;
        var className = 'traadvisning' + (melding.lest ? ' lest' : '');
        return (
            <a className={className} href={"/mininnboks/traad/" + melding.traadId}>
                <h2>{dato}</h2>
            {lestImg}
                <h3>{melding.statusTekst}</h3>
                <p>{melding.fritekst}</p>
                <AntallMeldinger antall={this.props.traad.antallHenvendelser} />
            </a>
        )
    }
});

module.exports = TraadVisning;