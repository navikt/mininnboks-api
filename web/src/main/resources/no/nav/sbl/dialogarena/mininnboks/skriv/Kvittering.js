var React = require('react/addons');
var Link = require('react-router').Link;


class Kvittering extends React.Component {
    render () {
        return (
            <article className="send-sporsmal-container bekreftelse-panel">
                <div className="sporsmal-header">
                    <img src="/mininnboks/build/img/hake_stor02.svg" alt="Kvitteringsikon"/>

                    <h2 className="stor deloverskrift">{this.props.resources.get('send-sporsmal.bekreftelse.antall-dager')}</h2>

                    <div className="robust-strek"></div>

                    <p dangerouslySetInnerHTML={{__html: this.props.resources.get('send-sporsmal.bekreftelse.varslingsinfo')}}></p>

                    <hr/>
                </div>
                <Link className="knapp-link-stor" to="innboks">{this.props.resources.get('send-sporsmal.bekreftelse.til-meldingsboksen')}</Link>
            </article>
        );
    }
};

export default Kvittering;