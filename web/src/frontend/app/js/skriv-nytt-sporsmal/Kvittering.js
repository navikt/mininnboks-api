import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';

class Kvittering extends React.Component {
    render() {
        const { formatMessage } = this.props;
    
        return (
            <article className="send-sporsmal-container bekreftelse-panel">
                <div className="sporsmal-header">
                    <img src="/mininnboks/build/img/hake_stor02.svg" alt="Kvitteringsikon"/>

                    <h2 className="stor deloverskrift">{formatMessage({ id: 'send-sporsmal.bekreftelse.antall-dager' })}</h2>

                    <div className="robust-strek"></div>

                    <p dangerouslySetInnerHTML={{ __html: formatMessage({ id: 'send-sporsmal.bekreftelse.varslingsinfo' }) }}></p>

                    <hr/>
                </div>
                <Link className="knapp-link-stor" to="/">{formatMessage({ id: 'send-sporsmal.bekreftelse.til-meldingsboksen' })}</Link>
            </article>
        );
    }
}

Kvittering.propTypes = {
    formatMessage: pt.func.isRequired
};

export default Kvittering;
