var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

var Melding = React.createClass({
    render: function () {
        var melding = this.props.melding;
        var dato = moment(melding.opprettet).format('Do MMMM YYYY, [kl.] HH:mm');

        var avsnitt = melding.fritekst.split(/[\r\n]+/).map(function (avsnitt) {
            return (<p>{avsnitt}</p>)
        });

        return (
            <div className="melding">
                <h2>{dato}</h2>
                <h3>{melding.statusTekst}</h3>
                <div className="fritekst">{avsnitt}</div>
            </div>
        )
    }
});

module.exports = Melding;