import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { shortDate, tilAvsnitt } from './../utils/Utils';
import Avsender from './AvsenderHeader';
import AntallMeldinger from './antall-meldinger';
import classNames from 'classnames';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart blokk-xxxs dialog', props.ulestMeldingKlasse, {
    markert: props.aktiv,
    'flere-meldinger': props.traad.length > 1
});

function MeldingPreview(props) {
    const { traad } = props;
    const melding = traad.nyeste;
    const avsender = traad.nyeste.fraNav ? <Avsender meldingType={melding.type} />: null;
    const dato = shortDate(melding.opprettet);
    const avsnitt = melding.fritekst.split(/[\r\n]+/).map(tilAvsnitt);
    const antallMeldinger = traad.meldinger.length;

    return (
        <li className="traad" key={melding.traadId}>
            <Link to={`/traad/${melding.traadId}`}
                  className={cls(props)}
            >
                <p className="vekk">
                    <FormattedMessage id="meldinger.ikon" />
                </p>
                <AntallMeldinger antall={antallMeldinger} />
                <div className="typo-normal blokk-xxxs">
                    <p><span key="dato">{dato}</span>{avsender}</p>
                    <h2 className="typo-element blokk-xxs">{melding.statusTekst}</h2>
                    <p className="typo-infotekst tema-avsnitt">{avsnitt}</p>
                </div>
            </Link>
        </li>
    );
}

MeldingPreview.propTypes = {
    traad: PT.object,
    aktiv: PT.bool.isRequired,
    ulestMeldingKlasse: PT.string
};

export default MeldingPreview;
