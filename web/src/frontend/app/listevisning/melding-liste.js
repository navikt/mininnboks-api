import React, { PropTypes as PT } from 'react';
import MeldingPreview from './melding-preview';
import DokumentPreview from './dokument-preview';
import { FormattedMessage } from 'react-intl';
import { Panel } from 'nav-react-design/dist/panel';
import { Undertittel } from 'nav-react-design/dist/tittel';

const MeldingListe = ({ meldinger, overskrift }) => {
    const innhold = meldinger.map((config) => {
        const type = config.traad.nyeste.type;
        const props = {
            aktiv: config.aktiv,
            key: config.traad.traadId,
            traad: config.traad,
            ulestMeldingKlasse: config.ulestMeldingKlasse
        };

        if (type === 'DOKUMENT_VARSEL') {
            return <DokumentPreview {...props} />;
        }
        return <MeldingPreview {...props} />;
    });

    return (
        <section className="traad-liste">
            <Panel className="panel blokk-xxxs">
                <Undertittel>
                    <FormattedMessage id={overskrift} values={{ antallMeldinger: meldinger.length }} />
                    <span className="vekk">({meldinger.length})</span>
                </Undertittel>
            </Panel>
            <ul className="ustilet">
                {innhold}
            </ul>
        </section>
    );
};

MeldingListe.propTypes = {
    meldinger: PT.array.isRequired,
    overskrift: PT.string.isRequired
};

export default MeldingListe;
