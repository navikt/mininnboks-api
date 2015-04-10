var React = require('react/addons');

var Epost = React.createClass({
    getDefaultProps: function(){
        return {
            erKvittering: false,
            className: '',
            linkClass: ''
        };
    },
    render: function() {
        var TPSFeil = !this.props.resources.hasKey('bruker.epost');
        var epost = this.props.resources.get('bruker.epost');

        if (TPSFeil) {
            return <p>{this.props.resources.get('send-sporsmal.bekreftelse.kunne-ikke-hente-epost')}</p>;
        }

        var infoTekst = epost ?
            this.props.resources.get(getCMSKey(this.props.erKvittering, 'du-mottar-epost')) :
            this.props.resources.get(getCMSKey(this.props.erKvittering, 'du-kan-motta-epost'));

        var epostTekst = epost ?
            <span>
                    {epost}
                    {' '}
                <a href={this.props.resources.get('brukerprofil.link')} className={this.props.linkClass}>{this.props.resources.get(getCMSKey(this.props.erKvittering, 'endre-epostadresse'))}</a>
            </span> :
            <a href={this.props.resources.get('brukerprofil.link')} className={this.props.linkClass}>{this.props.resources.get(getCMSKey(this.props.erKvittering, 'registrer-epostadresse'))}</a>;

        return (
            <p className={this.props.className}>{epostTekst} {infoTekst}</p>
        );
    }
});

function getCMSKey(erKvittering, key) {
    if (!erKvittering) {
        return 'traadvisning.send-svar.bekreftelse.'+key;
    } else {
        return 'send-sporsmal.bekreftelse.'+key;
    }
}

module.exports = Epost;
