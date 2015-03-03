var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

var TraadVisning = React.createClass({
    render: function () {
        var dato = moment(this.props.melding.opprettet.millis).format('Do MMMM YYYY, [kl.] HH:mm');
        return (
            <a className="traadvisning" href={"/mininnboks/traad/" + this.props.melding.traadId}>
                <h2>{dato}</h2>
                <h3>{this.props.melding.statusTekst}</h3>
                <p>{this.props.melding.fritekst}</p>
            </a>
        )
    }
});

module.exports = TraadVisning;