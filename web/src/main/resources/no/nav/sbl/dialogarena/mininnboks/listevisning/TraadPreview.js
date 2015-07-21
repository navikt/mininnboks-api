var React = require('react/addons');
var Link = require('react-router').Link;
var AntallMeldinger = require('./AntallMeldinger');
var MeldingStatus = require('./MeldingStatus');
var Utils = require('../utils/Utils');
var Constants = require('../utils/Constants');

var TraadPreview = React.createClass({
    onClick: function () {
        this.props.setValgtTraad(this.props.traad);
    },
    render: function () {
        var melding = this.props.traad.nyeste;
        var status = Utils.status(melding);
        var antallMeldinger = this.props.traad.meldinger.length;

        var dato = Utils.prettyDate(melding.opprettet);
        var avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.tilAvsnitt());
        avsnitt = React.addons.createFragment({
            avsnitt: avsnitt
        });

        var purring = melding.type === 'SPORSMAL_MODIA_UTGAAENDE' ?
            <span className="purring">{this.props.resources.get('purre.svar')}</span> : null;
        return (
            <Link to="traad" params={{traadId: melding.traadId}} className={'traadlistevisning ' + status}
                  onClick={this.onClick}>
                <AntallMeldinger antall={antallMeldinger}/>
                <MeldingStatus melding={melding}/>

                <div className="melding">
                    <p className="vekk">{skjermleserStatus[status]}</p>

                    <h3 className="status">{melding.statusTekst}</h3>

                    <p className="vekk">{skjermleserAntall(antallMeldinger)}</p>

                    <p className="dato">{dato}</p>
                    {purring}
                    <div className="fritekst">{avsnitt}</div>
                </div>
            </Link>
        )
    }
});

var skjermleserStatus = {};
skjermleserStatus[Constants.BESVART] = 'Besvart';
skjermleserStatus[Constants.IKKE_LEST] = 'Ikke lest';
skjermleserStatus[Constants.LEST_UBESVART] = 'Lest ubesvart';
skjermleserStatus[Constants.LEST] = 'Lest';

function skjermleserAntall(antall) {
    return antall + (antall == 1 ? ' melding' : ' meldinger');
}

module.exports = TraadPreview;