import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import IntlLenke from './../utils/intl-lenke';
import { nyesteTraadForst } from './../utils/utils';
import MeldingListe from './melding-liste';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/custom-breadcrumbs';

const getTraadLister = (traader) => {
    const sortert = traader.sort(nyesteTraadForst);
    const uleste = sortert.filter(traad => !traad.nyeste.lest);
    const leste = sortert.filter(traad => traad.nyeste.lest);

    return {
        uleste,
        leste
    };
};
const erAktivRegel = (fantVarselId, varselId) => {
    if (!fantVarselId) {
        return (_, index) => index === 0
    }
    return (melding) => melding.korrelasjonsId === varselId;
};


function ListeVisning({ routes, params, traader, location }) {
    const varselId = location.query.varselId;
    const traaderGruppert = getTraadLister(traader);

    const fantVarselId = traader.find((traad) => traad.nyeste.korrelasjonsId === varselId);
    const erAktiv = erAktivRegel(fantVarselId, varselId);

    const ulesteTraader = traaderGruppert.uleste.map((traad, index) => ({
        traad, aktiv: erAktiv(traad.nyeste, index), ulestMeldingKlasse: 'uleste-meldinger'
    }));
    const lesteTraader = traaderGruppert.leste.map((traad, index) => ({
        traad, aktiv: erAktiv(traad.nyeste, index + ulesteTraader.length)
    }));
    
    const ulesteMeldingerOverskrift = ulesteTraader.length === 0 ? 'innboks.uleste.ingenmeldinger' : 'innboks.uleste.tittel';
    const lesteMeldingerOverskrift = lesteTraader.length === 0 ? 'innboks.leste.ingenmeldinger' : 'innboks.leste.tittel';

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
            <MeldingListe meldinger={ulesteTraader} overskrift={ulesteMeldingerOverskrift} />
            <MeldingListe meldinger={lesteTraader} overskrift={lesteMeldingerOverskrift} />
        </div>
    );
}

ListeVisning.propTypes = {
    traader: PT.array.isRequired
};

const mapStateToProps = ({ traader }) => ({ traader });

export default connect(mapStateToProps)(ListeVisning);
