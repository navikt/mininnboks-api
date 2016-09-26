import React, { Component, PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { shortDate, safeHtml } from '../utils';
import AntallMeldinger from './antall-meldinger';
import classNames from 'classnames';
import history from './../history';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart dialog', props.ulestMeldingKlasse, {
    markert: props.aktiv,
    'flere-meldinger': props.traad.meldinger.length > 1
});

class MeldingPreview extends Component {
    componentDidMount() {
        if (this.props.aktiv) {
            history.push(`traad/${this.props.traad.nyeste.id}`);
        }
    }

    render() {
        const { traad } = this.props;

        const melding = traad.nyeste;
        const dato = shortDate(melding.opprettet);
        const fritekst = safeHtml(melding.fritekst);

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
                    ref="lenke"
                    to={`/traad/${melding.traadId}`}
                    className={cls(this.props)}
                >
                    <p className="vekk">
                        <FormattedMessage id="meldinger.ikon" />
                    </p>
                    <AntallMeldinger antall={antallMeldinger} />
                    <div className="typo-normal">
                        <p className="blokk-xxxs">
                            <span>{dato}</span>
                            {avsender}
                            {maBesvares}
                        </p>
                        <h3 className="typo-element blokk-xxs">
                            {melding.statusTekst}
                            <span className="vekk">
                                {flereMeldinger}
                                {maBesvares}
                            </span>
                        </h3>
                        <p className="typo-infotekst tema-avsnitt nettobunn">{fritekst}</p>
                    </div>
                </Link>
            </li>
        );
    }
}

MeldingPreview.propTypes = {
    traad: PT.object,
    aktiv: PT.bool.isRequired,
    ulestMeldingKlasse: PT.string
};

export default MeldingPreview;
