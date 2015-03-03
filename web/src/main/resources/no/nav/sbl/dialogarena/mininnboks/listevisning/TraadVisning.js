var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

var TraadVisning = React.createClass({
    render: function () {
        var melding = this.props.traad.nyesteHenvendelse;
        var dato = moment(melding.opprettet.millis).format('Do MMMM YYYY, [kl.] HH:mm');

        var lestImg = !melding.lest ? <img src="/mininnboks/img/melding_ny.svg" className="melding-info" /> : null;

        var antall = this.props.traad.antallHenvendelser;
        var antallImg = null;
        if (antall == 2) {
            antallImg = <img src="/mininnboks/img/melding_ett%20innlegg.svg" className="antall"/>
        } else if (antall > 2) {
            antallImg = <img src="/mininnboks/img/melding_flere%20innlegg.svg" className="antall"/>
        }

        return (
            <a className="traadvisning" href={"/mininnboks/traad/" + melding.traadId}>
                <h2>{dato}</h2>
            {lestImg}
                <h3>{melding.statusTekst}</h3>
                <p>{melding.fritekst}</p>
            {antallImg}
            </a>
        )
    }
});

module.exports = TraadVisning;