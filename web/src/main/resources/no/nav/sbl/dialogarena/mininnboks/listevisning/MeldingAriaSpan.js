var React = require('react/addons');
var format = require('string-format');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

var MeldingAriaSpan = React.createClass({
    render: function () {
        var behandlingsStatus = '';
        if (this.props.melding.type === 'SPORSMAL_MODIA_UTGAAENDE' || !this.props.melding.lest) {
            behandlingsStatus += 'Ubehandlet,';
        } else if (this.props.melding.type == 'SVAR_SBL_INNGAAENDE') {
            behandlingsStatus += 'Besvart,';
        }
        var aria = format('{0} {1} {2}, {3}, {4}',
            behandlingsStatus,
            this.props.antall,
            this.props.antall === 1 ? 'melding' : 'meldinger',
            moment(this.props.melding.opprettet).format('Do MMMM YYYY, [kl.] HH:mm'),
            this.props.melding.statusTekst
        );

        return (
            <span className="vekk">
                {aria}
            </span>
        );
    }
});

module.exports = MeldingAriaSpan;
