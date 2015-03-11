var React = require('react');
var moment = require('moment');
var Utils = require('utils');
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

        var avsnitt = Utils.sanitize(melding.fritekst).split(/[\r\n]+/)
            .map(this.props.lagLenkerAvURL ? Utils.leggTilLenkerTags : Utils.voidTransformation)
            .map(Utils.tilParagraf(this.props.lagLenkerAvURL));

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