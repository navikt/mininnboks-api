import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { shortDate } from './../utils/utils';

import classNames from 'classnames';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart blokk-xxxs dokument', props.ulestMeldingKlasse, {
    markert: props.aktiv
});

function DokumentPreview(props) {
    const { traad } = props;
    const dokument = traad.nyeste;
    const avsender = <span className="avsender-fra-nav"><FormattedMessage id="avsender.tekst.NAV" /></span>;
    const dato = shortDate(dokument.opprettet);
    const temanavn = dokument.temaNavn;

    return (
        <li className="traad">
            <Link
                to={`/dokument/${dokument.id}`}
                className={cls(props)}
            >
                <p className="vekk"><FormattedMessage id="dokumentmelding.ikon" /></p>
                <div className="typo-normal blokk-xxxs">
                    <p>{dato} / Fra {avsender} </p>
                    <h2 className="typo-element blokk-xxs">{dokument.statusTekst}</h2>
                    <p className="typo-infotekst tema-dokument">{temanavn}</p>
                </div>
            </Link>
        </li>
    );
}

DokumentPreview.propTypes = {
    traad: PT.object,
    aktiv: PT.bool.isRequired,
    ulestMeldingKlasse: PT.string
};

export default DokumentPreview;
