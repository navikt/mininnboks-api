import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';

function Kvittering() {
    const htmlInnhold = <FormattedMessage id="send-sporsmal.bekreftelse.varslingsinfo" />;
    return (
        <article className="send-sporsmal-container bekreftelse-panel">
            <div className="sporsmal-header">
                <img src="/mininnboks/build/img/hake_stor02.svg" alt="Kvitteringsikon" />

                <h2 className="stor deloverskrift">
                    <FormattedMessage id="send-sporsmal.bekreftelse.antall-dager" />
                </h2>

                <div className="robust-strek" />

                <p dangerouslySetInnerHTML={{ __html: htmlInnhold }} />

                <hr />
            </div>
            <Link className="knapp-link-stor" to="/">
                <FormattedMessage id="send-sporsmal.bekreftelse.til-meldingsboksen" />
            </Link>
        </article>
    );
}

Kvittering.propTypes = {
};

export default Kvittering;
