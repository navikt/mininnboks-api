import React from 'react';
import { FormattedMessage, FormattedHTMLMessage } from 'react-intl';
import { Link } from 'react-router';

function Kvittering() {
    return (
        <article className="send-sporsmal-container bekreftelse-panel">
            <div className="sporsmal-header">
                <img src="/mininnboks/img/hake_stor02.svg" alt="Kvitteringsikon" />

                <h2 className="stor deloverskrift">
                    <FormattedMessage id="send-sporsmal.bekreftelse.antall-dager" />
                </h2>

                <div className="robust-strek" />

                <p>
                    <FormattedHTMLMessage id="send-sporsmal.bekreftelse.varslingsinfo" />
                </p>

                <hr />
            </div>
            <Link className="knapp-link-stor" to="/">
                <FormattedMessage id="send-sporsmal.bekreftelse.til-meldingsboksen" />
            </Link>
        </article>
    );
}

export default Kvittering;
