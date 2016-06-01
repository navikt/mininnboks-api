import React from 'react';
import { Link } from 'react-router';
import Utils from '../utils/Utils';

class TraadPreview extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick() {
        this.props.setValgtTraad(this.props.traad);
    }

    render() {
        const { traad, index, formatMessage } = this.props;
        const markertKlasse = index === 0 ? 'markert' : '';
        const melding = traad.nyeste;
        const temagruppenavn = traad.nyeste.temagruppeNavn;
        const midlertidigAvsendernavn = 'Bruker'.toLowerCase();
        const avsender = traad.nyeste.fraNav ? 
            <span className="avsender-fra-nav">{formatMessage({ id: 'avsender.tekst.NAV' } )}</span> : 
            <span className="avsender-annen">{midlertidigAvsendernavn}</span>;
        const dato = Utils.shortDate(melding.opprettet);

        return (
            <li className="traad">
                <Link to={`/mininnboks/traad/${melding.traadId}`} className={`panel panel-ikon panel-klikkbart blokk-xxxs dialog ${markertKlasse}`}
                  onClick={this.onClick}>
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

export default TraadPreview;
