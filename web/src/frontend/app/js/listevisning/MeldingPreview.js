import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import { shortDate, tilAvsnitt } from './../utils/Utils';
import Avsender from './AvsenderHeader';
import AntallMeldinger from './AntallMeldinger';

class MeldingPreview extends React.Component {
    render() {
        const { traad, aktiv, onClick, formatMessage, ulestMeldingKlasse } = this.props;
        const markertKlasse = aktiv ? 'markert' : '';
        const melding = traad.nyeste;
        const temagruppenavn = melding.temagruppeNavn;
        const avsender = traad.nyeste.fraNav ? <Avsender meldingType={melding.type} formatMessage={formatMessage}/> : <noscript/>;
        const dato = shortDate(melding.opprettet);
        const avsnitt = melding.fritekst.split(/[\r\n]+/).map(tilAvsnitt);
        const antallMeldinger = traad.meldinger.length;
        const flereMeldingerKlasse = antallMeldinger > 1 ? 'flere-meldinger' : '';

        return (
            <li className="traad" key={melding.traadId}>
                <Link to={`/traad/${temagruppenavn}/${melding.traadId}`} onClick={onClick}
                  className={`panel panel-ikon panel-klikkbart blokk-xxxs dialog ${markertKlasse} ${flereMeldingerKlasse} ${ulestMeldingKlasse}`}
                >
                    <p className="vekk">{formatMessage({ id: 'meldinger.ikon' })}</p>
                    <AntallMeldinger antall={antallMeldinger} formatMessage={formatMessage} />
                    <div className="typo-normal blokk-xxxs">
                        <p><span key="dato">{dato}</span>{avsender}</p>
                        <h2 className="typo-element blokk-xxs">{melding.statusTekst}</h2>
                        <p className="typo-infotekst tema-avsnitt">{avsnitt}</p>
                    </div>
                </Link>
            </li>
        );
    }
}

MeldingPreview.propTypes = {
    traad: pt.object,
    formatMessage: pt.func.isRequired,
    aktiv: pt.bool.isRequired,
    onClick: pt.func.isRequired,
    ulestMeldingKlasse: pt.string
};

export default MeldingPreview;
