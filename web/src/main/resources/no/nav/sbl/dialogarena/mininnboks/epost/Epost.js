var React = require('react/addons');

var Epost = React.createClass({
    render: function() {
        var TPSFeil = !this.props.resources.hasKey('bruker.epost');
        var epost = this.props.resources.get('bruker.epost');

        console.log('epost', TPSFeil, epost);

        if (TPSFeil) {
            return <p>{this.props.resources.get('send-sporsmal.bekreftelse.kunne-ikke-hente-epost')}</p>;
        }

        var infoTekst = epost ?
            this.props.resources.get('traadvisning.send-svar.bekreftelse.du-mottar-epost') :
            this.props.resources.get('traadvisning.send-svar.bekreftelse.du-kan-motta-epost');

        console.log('infotekst', infoTekst)
        var epostTekst = epost ?
            <span>
                    {epost}
                    {' '}
                <a href={this.props.resources.get('brukerprofil.link')}>{this.props.resources.get('traadvisning.send-svar.bekreftelse.endre-epostadresse')}</a>
            </span> :
            <a href={this.props.resources.get('brukerprofil.link')}>{this.props.resources.get('traadvisning.send-svar.bekreftelse.registrer-epostadresse')}</a>;

        return (
            <p>{infoTekst} {epostTekst}</p>
        );
    }
});

module.exports = Epost;
