var React = require('react/addons');
var Link = require('react-router').Link;

var Kvittering = React.createClass({
    render: function () {
        return (
            <article className="send-sporsmal-container bekreftelse-panel">
                <div className="sporsmal-header">
                    <img src="/mininnboks/build/img/hake_stor02.svg" alt="Kvitteringsikon"/>

                    <h2 className="stor deloverskrift">{this.props.resources.get('send-sporsmal.bekreftelse.antall-dager')}</h2>

                    <div className="robust-strek"></div>


                    <div className="mangler-epost">
                        {this.props.resources.get('send-sporsmal.bekreftelse.du-kan-motta-epost')}
                        <a className="brukerprofil-link" href={this.props.resources.get('brukerprofil.link')}>
                            {this.props.resources.get('send-sporsmal.bekreftelse.registrer-epostadresse')}
                        </a>
                    </div>


                    <hr/>
                </div>
                <Link className="knapp-link-stor" to="innboks">{this.props.resources.get('send-sporsmal.bekreftelse.til-meldingsboksen')}</Link>
            </article>
        );
    }
});

module.exports = Kvittering;