var React = require('react/addons');
var moment = require('moment');
var Utils = require('../utils/Utils');
require('moment/locale/nb');
moment.locale('nb');

var Melding = React.createClass({
    geDefaultProps: function () {
        return {
            lagLenkerAvURL: false
        }
    },
    render: function () {
        var melding = this.props.melding;
        var dato = moment(melding.opprettet).format('Do MMMM YYYY, [kl.] HH:mm');

        var avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(this.props.lagLenkerAvURL ? Utils.leggTilLenkerTags : Utils.voidTransformation)
            .map(Utils.tilParagraf(this.props.lagLenkerAvURL));
        avsnitt = React.addons.createFragment({
            avsnitt: avsnitt
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