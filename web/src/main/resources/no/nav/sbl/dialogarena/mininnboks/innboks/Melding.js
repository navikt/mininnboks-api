var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

var Melding = React.createClass({
    render: function() {
        var melding = this.props.melding;
        var dato = moment(melding.opprettet).format('Do MMMM YYYY, [kl.] HH:mm');

        return (
            <div className="melding">
                <h2>{dato}</h2>
                <h3>{melding.statusTekst}</h3>
                <p>{melding.fritekst}</p>
            </div>
        )
    }
});

module.exports = Melding;