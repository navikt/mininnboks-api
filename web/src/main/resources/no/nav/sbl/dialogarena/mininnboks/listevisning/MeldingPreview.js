import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import Utils from '../utils/Utils';

class MeldingPreview extends React.Component {
    render() {
        const { traad, aktiv, onClick, formatMessage } = this.props;
        const markertKlasse = aktiv ? 'markert' : '';
        const melding = traad.nyeste;
        const temagruppenavn = traad.nyeste.temagruppeNavn;
        const midlertidigAvsendernavn = 'Bruker'.toLowerCase();
        const avsender = traad.nyeste.fraNav ?
            <span className="avsender-fra-nav">{formatMessage({ id: 'avsender.tekst.NAV' })}</span> :
            <span className="avsender-annen">{midlertidigAvsendernavn}</span>;
        const dato = Utils.shortDate(melding.opprettet);

        return (
            <li className="traad">
                <Link to={`/traad/${temagruppenavn}/${melding.traadId}`} onClick={onClick}
                      className={`panel panel-ikon panel-klikkbart blokk-xxxs dialog ${markertKlasse}`}
                >
                    <div className="typo-normal blokk-xxxs">
                        <p>{dato} / Fra {avsender} </p>
                        <h2 className="typo-element blokk-xxs">{melding.statusTekst}</h2>
                        <p className="typo-infotekst tema-dokument">{temagruppenavn}</p>
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
