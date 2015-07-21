var React = require('react/addons');
var moment = require('moment');
var Utils = require('../utils/Utils');
require('moment/locale/nb');
moment.locale('nb');

var Melding = React.createClass({
    geDefaultProps: function () {
        return {
            purreSvar: false,
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
        var purring = this.props.purreSvar && melding.type === 'SPORSMAL_MODIA_UTGAAENDE' ? <span className="purring">{this.props.resources.get('purre.svar')}</span> : null;
        return (
            <div className="melding">
                {traadinfo}
                <h3 className="status">{melding.statusTekst}</h3>
                <p className="dato">{dato}</p>
                {purring}
                <div className="fritekst">{avsnitt}</div>
            </div>
        )
    }
});

module.exports = Melding;