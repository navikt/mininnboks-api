import React, { Component, PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import classNames from 'classnames';
import { shortDate, safeHtml } from '../utils';
import history from './../history';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart oppgave', props.ulestMeldingKlasse, {
    markert: props.aktiv
});

class OppgavePreview extends Component {
    componentDidMount() {
        if (this.props.aktiv) {
            history.replace(`oppgave/${this.props.traad.nyeste.id}`);
        }
    }

    render() {
        const { traad } = this.props;

        const melding = traad.nyeste;
        const dato = shortDate(melding.opprettet);
        const avsnitt = safeHtml(melding.fritekst);

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
                        <FormattedMessage id="oppgavemelding.ikon" />
                    </p>
                    <div className="typo-normal">
                        <p className="blokk-xxxs">
                            <span>{dato}</span>
                            {avsender}
                        </p>
                        <h3 className="typo-element blokk-xxs">
                            {melding.statusTekst}
                        </h3>
                        <p className="typo-infotekst tema-avsnitt nettobunn">{avsnitt}</p>
                    </div>
                </Link>
            </li>
        );
    }
}

OppgavePreview.propTypes = {
    traad: PT.object,
    aktiv: PT.bool.isRequired,
    ulestMeldingKlasse: PT.string
};

export default OppgavePreview;
