var React = require('react');

var MeldingStatus = React.createClass({
    render: function () {
        var melding = this.props.melding;
        var status = 'ikon melding-info';
        var statusTekst;

        if (melding.type === 'SPORSMAL_MODIA_UTGAAENDE' || !melding.lest) {
            status += ' ubehandlet';
            statusTekst = 'Ubehandlet';
        } else if (melding.type == 'SVAR_SBL_INNGAAENDE') {
            status += ' besvart';
            statusTekst = 'Besvart';
        } else {
            return null;
        }

        return (
            <div>
                <p className="vekk">{statusTekst}</p>
                <div className={status} aria-label={statusTekst}/>
            </div>
        );
    }
});

module.exports = MeldingStatus;