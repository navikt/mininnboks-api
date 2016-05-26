import React from 'react/addons';
import { Link } from 'react-router';
import AntallMeldinger from './AntallMeldinger';
import MeldingStatus from './MeldingStatus';
import Utils from '../utils/Utils';
import Constants from '../utils/Constants';

class TraadPreview extends React.Component {
    constructor (props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick () {
        this.props.setValgtTraad(this.props.traad);
    }

    render () {
        const { traad } = this.props;
        var melding = traad.nyeste;
        var temagruppenavn = traad.nyeste.temagruppeNavn;
        var midlertidigAvsendernavn = "Bruker".toLowerCase();
        var avsender = traad.nyeste.fraNav ? <span className="avsender-fra-nav">NAV</span> : <span className="avsender-annen"> {midlertidigAvsendernavn}</span>;
        var dato = Utils.shortDate(melding.opprettet);

        return (
            <li className="traad">
                <Link to={`/mininnboks/traad/${melding.traadId}`} className={'panel panel-ikon panel-klikkbart blokk-xxxs dialog'}
                  onClick={this.onClick}>
                    <div className="melding">
                        <p className="dato">{dato} / Fra {avsender} </p>
                        <h2 className="typo-element blokk-xxs">{melding.statusTekst}</h2>
                        <p className="temagruppenavn">{temagruppenavn}</p>
                    </div>
                </Link>
            </li>
        );
    }
};

export default TraadPreview;