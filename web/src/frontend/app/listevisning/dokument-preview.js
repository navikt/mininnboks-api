import React, { Component, PropTypes as PT } from 'react';
import { findDOMNode } from 'react-dom';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { shortDate } from '../utils';

import classNames from 'classnames';

const cls = (props) => classNames('panel panel-ikon panel-klikkbart blokk-xxxs dokument', props.ulestMeldingKlasse, {
    markert: props.aktiv
});

class DokumentPreview extends Component {
    componentDidMount() {
        if (this.props.aktiv) {
            findDOMNode(this.refs.lenke).focus();
        }
    }

    render() {
        const { traad } = this.props;
        const dokument = traad.nyeste;
        const avsender = <span className="avsender-fra-nav"><FormattedMessage id="avsender.tekst.NAV" /></span>;
        const dato = shortDate(dokument.opprettet);
        const temanavn = dokument.temaNavn;

        return (
            <li className="traad">
                <Link
                    ref="lenke"
                    to={`/dokument/${dokument.id}`}
                    className={cls(this.props)}
                >
                    <p className="vekk"><FormattedMessage id="dokumentmelding.ikon" /></p>
                    <div className="typo-normal">
                        <p className="blokk-xxxs">{dato} / Fra {avsender} </p>
                        <h3 className="typo-element blokk-xxs">{dokument.statusTekst}</h3>
                        <p className="typo-infotekst tema-avsnitt nettobunn">{temanavn}</p>
                    </div>
                </Link>
            </li>
        );
    }
}

DokumentPreview.propTypes = {
    traad: PT.object,
    aktiv: PT.bool.isRequired,
    ulestMeldingKlasse: PT.string
};

export default DokumentPreview;
