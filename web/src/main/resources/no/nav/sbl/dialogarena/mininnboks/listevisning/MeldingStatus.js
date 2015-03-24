var React = require('react');

var MeldingStatus = React.createClass({
    render: function () {
        var melding = this.props.melding;
        var status = 'ikon melding-info';
        var statusTekst = "Meldingen er";

        if (melding.type === 'SPORSMAL_MODIA_UTGAAENDE' || !melding.lest) {
            status += ' ubehandlet';
            statusTekst += ' ubehandlet.';
        } else if (melding.type == 'SVAR_SBL_INNGAAENDE') {
            status += ' besvart';
            statusTekst += ' besvart.';
        } else {
            return null;
        }

        return <div className={status} aria-label={statusTekst}/>;
    }
});

module.exports = MeldingStatus;