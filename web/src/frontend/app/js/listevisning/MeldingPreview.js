import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import { shortDate, tilAvsnitt } from './../utils/Utils';
import Avsender from './AvsenderHeader';

class MeldingPreview extends React.Component {
    render() {
        const { traad, aktiv, onClick, formatMessage } = this.props;
        const markertKlasse = aktiv ? 'markert' : '';
        const melding = traad.nyeste;
        const temagruppenavn = melding.temagruppeNavn;
        const avsender = traad.nyeste.fraNav ? <Avsender meldingType={melding.type} formatMessage={formatMessage}/> : <noscript/>;
        const dato = shortDate(melding.opprettet);
        const avsnitt = melding.fritekst.split(/[\r\n]+/).map(tilAvsnitt);

        const flereMeldingerKlasse = traad.meldinger.length > 1 ? 'flere-meldinger' : '';

        return (
            <li className="traad">
                <Link to={`/traad/${temagruppenavn}/${melding.traadId}`} onClick={onClick}
                  className={`panel panel-ikon panel-klikkbart blokk-xxxs dialog ${markertKlasse} ${flereMeldingerKlasse}`}
                >
                    <div className="typo-normal blokk-xxxs">
                        <p><span>{dato}</span>{avsender}</p>
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
    onClick: pt.func.isRequired
};

export default MeldingPreview;
