var React = require('react/addons');

var MeldingStatus = React.createClass({
    render: function () {
        var melding = this.props.melding;
        var status = 'ikon melding-info';

        if (melding.type === 'SPORSMAL_MODIA_UTGAAENDE' || !melding.lest) {
            status += ' ubehandlet';
        } else if (melding.type == 'SVAR_SBL_INNGAAENDE') {
            status += ' besvart';
        } else {
            return null;
        }

        return (
            <div aria-hidden="true" className={status}></div>
        );
    }
});

module.exports = MeldingStatus;