import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import Utils from '../utils/Utils';

class DokumentPreview extends React.Component {

    render() {
        const { traad, index, formatMessage } = this.props;
        const markertKlasse = index === 0 ? 'markert' : '';
        const dokument = traad.nyeste;
        const avsender =  <span className="avsender-fra-nav">{formatMessage({ id: 'avsender.tekst.NAV' })}</span>;
        const dato = Utils.shortDate(dokument.opprettet);
        const temanavn = dokument.temaNavn;

        return (
            <li className="traad">
                <Link to={`/dokument/${dokument.id}`} onClick={this.props.onClick}
                  className={`panel panel-ikon panel-klikkbart blokk-xxxs dokument ${markertKlasse}`}
                >
                    <div className="typo-normal blokk-xxxs">
                        <p>{dato} / Fra {avsender} </p>
                        <h2 className="typo-element blokk-xxs">{dokument.statusTekst}</h2>
                        <p className="typo-infotekst tema-dokument">{temanavn}</p>
                    </div>
                </Link>
            </li>
        );
    }
}

DokumentPreview.propTypes = {
    traad: pt.object,
    formatMessage: pt.func.isRequired
};

export default DokumentPreview;
