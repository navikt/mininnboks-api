import React, { Component, PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { shortDate, tilAvsnitt } from '../utils';
import classNames from 'classnames';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart oppgave', props.ulestMeldingKlasse, {
    markert: props.aktiv
});

class OppgavePreview extends Component {
    componentDidMount() {
        if (this.props.aktiv) {
            window.location.replace(this.props.traad.nyeste.oppgaveUrl);
        }
    }

    render() {
        const { traad } = this.props;

        const melding = traad.nyeste;
        const dato = shortDate(melding.opprettet);
        const avsnitt = melding.fritekst.split(/[\r\n]+/).map(tilAvsnitt);

        const avsender = traad.nyeste.fraNav ? (
            <span>/ Fra <span className="avsender-fra-nav"><FormattedMessage id="avsender.tekst.NAV" /></span></span>
        ) : null;

        return (
            <li className="traad blokk-xxxs" key={melding.traadId}>
                <Link
                    ref="lenke"
                    to={`/oppgave/${melding.traadId}`}
                    className={cls(this.props)}
                >
                    <p className="vekk">
                        <FormattedMessage id="meldinger.ikon" />
                    </p>
                    <div className="typo-normal blokk-xxxs">
                        <p className="blokk-xxs">
                            <span>{dato}</span>
                            {avsender}
                        </p>
                        <h2 className="typo-element blokk-xxs">
                            {melding.statusTekst}
                        </h2>
                        <p className="typo-infotekst tema-avsnitt nettobunn">{avsnitt}</p>
                    </div>
                </Link>
            </li>
        );
    }
}

OppgavePreview.propTypes = {
    router: PT.object.isRequired,
    traad: PT.object,
    aktiv: PT.bool.isRequired,
    ulestMeldingKlasse: PT.string
};

export default OppgavePreview;
