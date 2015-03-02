var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

var TraadVisning = React.createClass({
    render: function () {
        var dato = moment(this.props.melding.opprettet.millis).format('Do MMMM YYYY, [kl.] HH:mm');
        return (
            <section className="traadvisning">
                <h2>{dato}</h2>
                <h3>{this.props.melding.statusTekst} om {this.props.melding.temagruppe}</h3>
                <p>{this.props.melding.fritekst}</p>
            </section>
        )
    }
});

module.exports = TraadVisning;