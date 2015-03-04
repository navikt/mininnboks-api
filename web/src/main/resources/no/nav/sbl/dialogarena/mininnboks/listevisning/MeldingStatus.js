var React = require('react');

var MeldingStatus = React.createClass({
    render: function () {
        var melding = this.props.melding;
        if (!melding.lest) {
            return (<img src="/mininnboks/img/melding_ny.svg" className="ikon melding-info" />)
        } else if (melding.type === 'SVAR_SBL_INNGAAENDE') {
            return (<img src="/mininnboks/img/melding_besvart.svg" className="ikon melding-info" />)
        }
        else return null;
    }
});

module.exports = MeldingStatus;