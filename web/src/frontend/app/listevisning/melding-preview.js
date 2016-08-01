import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { shortDate, tilAvsnitt } from './../utils/utils';
import AntallMeldinger from './antall-meldinger';
import classNames from 'classnames';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart dialog', props.ulestMeldingKlasse, {
    markert: props.aktiv,
    'flere-meldinger': props.traad.meldinger.length > 1
});

function MeldingPreview(props) {
    const { traad } = props;
    console.log('traad', traad);

    const melding = traad.nyeste;
    const dato = shortDate(melding.opprettet);
    const avsnitt = melding.fritekst.split(/[\r\n]+/).map(tilAvsnitt);
    const antallMeldinger = traad.meldinger.length;

    const maBesvares = melding.type === 'SPORSMAL_MODIA_UTGAAENDE' ?
        <span>/ <strong className="purring"><FormattedMessage id="purre.svar" /></strong></span> : null;

    const avsender = traad.nyeste.fraNav ? (
        <span>/ Fra <span className="avsender-fra-nav"><FormattedMessage id="avsender.tekst.NAV" /></span></span>
    ) : null;
    const flereMeldinger = antallMeldinger > 1 ? `(${antallMeldinger})` : null;

    return (
        <li className="traad blokk-xxxs" key={melding.traadId}>
            <Link
                to={`/traad/${melding.traadId}`}
                className={cls(props)}
            >
                <p className="vekk">
                    <FormattedMessage id="meldinger.ikon" />
                </p>
                <AntallMeldinger antall={antallMeldinger} />
                <div className="typo-normal blokk-xxxs">
                    <p>
                        <span>{dato}</span>
                        {avsender}
                        {maBesvares}
                    </p>
                    <h2 className="typo-element blokk-xxs">
                        {melding.statusTekst}
                        <span className="vekk">
                            {flereMeldinger}
                            {maBesvares}
                        </span>
                    </h2>
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
