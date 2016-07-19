import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import IntlLenke from './../utils/intl-lenke';
import TraaderContainer from './traad-container';
import Feilmelding from '../feilmelding/feilmelding';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/customBreadcrumbs';


function ListeVisning({ routes, params, traader, location }) {
    let content;
    if (traader.length === 0) {
        content = (
            <Feilmelding>
                <h1><FormattedMessage id="innboks.tom-innboks-melding"/></h1>
            </Feilmelding>
        );
    } else {
        content = <TraaderContainer traader={traader} location={location}/>;
    }

    return (
        <div>
            <Breadcrumbs routes={routes} params={params} />
            <h1 className="typo-sidetittel text-center blokk-l">
                <FormattedMessage id="innboks.overskrift"/>
            </h1>
            <div className="innboks-navigasjon clearfix">
                <IntlLenke href="skriv.ny.link" className="knapp knapp-hoved knapp-liten">
                    <FormattedMessage id="innboks.skriv.ny.link"/>
                </IntlLenke>
            </div>
            {content}
        </div>
    );
}

ListeVisning.propTypes = {
    traader: PT.array.isRequired
};

const mapStateToProps = ({ traader }) => ({ traader });

export default connect(mapStateToProps)(ListeVisning);
