import React, { PropTypes as PT } from 'react';
import { Personalia, Dokumenter, Hurtignavigering } from 'react-dokumentvisning';
import { FormattedMessage, injectIntl } from 'react-intl';
import FixedPosition from './../utils/fixed-position';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';

const offset = [-300, -80];

class DokumentVisning extends React.Component {
    componentDidMount() {
        document.body.scrollTop = 1;
        document.documentElement.scrollTop = 1;
    }

    render() {
        const { dokumentmetadata, journalpostmetadata, intl,
                routes, params, lastNedPdfOnClick, printPdfOnClick } = this.props;
        const { temakode } = journalpostmetadata.resultat;
        const kontaktNavUrl = intl.messages['dokumentvisning.kontakt.nav.link'];


        return (
            <div>
                <Breadcrumbs routes={routes} params={params} />
                <section className="dokumentvisning-header blokk-m">
                    <Personalia journalpostmetadata={journalpostmetadata.resultat} hode={false} />
                    <ul className="ustilet">
                        <li>
                            <a href={`/saksoversikt/app/tema/${temakode}`}>
                                <FormattedMessage id="dokumentvisning.gatil.saksoversikt" />
                            </a>
                        </li>
                        <li>
                            <a href={kontaktNavUrl}><FormattedMessage id="dokumentvisning.kontakt.nav" /></a>
                        </li>
                    </ul>
                </section>
                <section className="dokumenter">
                    <FixedPosition>
                        <Hurtignavigering dokumentmetadata={dokumentmetadata} navigeringsknappOffset={offset} />
                    </FixedPosition>
                    <Dokumenter
                        journalpostId={journalpostmetadata.resultat.journalpostId}
                        dokumentmetadata={dokumentmetadata}
                        lastNedPdfOnClick={lastNedPdfOnClick}
                        printPdfOnClick={printPdfOnClick}
                    />
                </section>
            </div>
        );
    }
}

DokumentVisning.propTypes = {
    dokumentmetadata: PT.array.isRequired,
    lastNedPdfOnClick: React.PropTypes.func.isRequired,
    printPdfOnClick: React.PropTypes.func.isRequired,
    journalpostmetadata: PT.object.isRequired,
    params: PT.object.isRequired,
    routes: PT.array.isRequired,
    intl: PT.object.isRequired
};

export default injectIntl(DokumentVisning);
