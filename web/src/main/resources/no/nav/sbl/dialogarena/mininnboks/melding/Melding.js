var React = require('react/addons');
var moment = require('moment');
var Utils = require('../utils/Utils');
require('moment/locale/nb');
moment.locale('nb');

var Melding = React.createClass({
    geDefaultProps: function () {
        return {
            lagLenkerAvURL: false,
            traadinfo: undefined
        }
    },
    render: function () {
        var melding = this.props.melding;
        var dato = moment(melding.opprettet).format('Do MMMM YYYY, [kl.] HH:mm');
        var medUrl = function (innhold) {
            return this.props.lagLenkerAvURL ? Utils.leggTilLenkerTags(innhold) : innhold;
        }.bind(this);

        var avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(medUrl)
            .map(Utils.tilAvsnitt(this.props.lagLenkerAvURL));
        avsnitt = React.addons.createFragment({
            avsnitt: avsnitt
        });

        var traadinfo = this.props.traadinfo ? <p className="vekk">{this.props.traadinfo}</p> : null;
        return (
            <div className="melding">
                <h2>{dato}</h2>
                {traadinfo}
                <p className="h3">{melding.statusTekst}</p>
                <div className="fritekst">{avsnitt}</div>
            </div>
        )
    }
});

module.exports = Melding;