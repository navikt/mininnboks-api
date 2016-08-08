import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import IntlLenke from './../utils/intl-lenke';
import { nyesteTraadForst } from '../utils';
import MeldingListe from './melding-liste';
import { connect } from 'react-redux';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';
import VisibleIf from './../utils/hocs/visible-if';
import { storeShape, traadShape } from './../proptype-shapes';
import { Sidetittel } from 'nav-react-design/dist/tittel';


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
        return (_, index) => index === 0;
    }
    return (melding) => melding.korrelasjonsId === varselId;
};


function ListeVisning({ routes, params, traader, location }) {
    const varselId = location.query.varselId;
    const traaderGruppert = getTraadLister(traader.data);

    const fantVarselId = traader.data.find((traad) => traad.nyeste.korrelasjonsId === varselId);
    const erAktiv = erAktivRegel(fantVarselId, varselId);

    const ulesteTraader = traaderGruppert.uleste.map((traad, index) => ({
        traad, aktiv: erAktiv(traad.nyeste, index), ulestMeldingKlasse: 'uleste-meldinger'
    }));
    const lesteTraader = traaderGruppert.leste.map((traad, index) => ({
        traad, aktiv: erAktiv(traad.nyeste, index + ulesteTraader.length)
    }));

    return (
        <div>
            <Breadcrumbs routes={routes} params={params} />
            <Sidetittel className="text-center blokk-l">
                <FormattedMessage id="innboks.overskrift" />
            </Sidetittel>
            <div className="text-center blokk-l">
                <IntlLenke href="skriv.ny.link" className="knapp knapp-hoved knapp-liten">
                    <FormattedMessage id="innboks.skriv.ny.link" />
                </IntlLenke>
            </div>
            <VisibleIf visibleIf={traader.data.length === 0}>
                <h2 className="typo-undertittel text-center">
                    <FormattedMessage id="innboks.tom-innboks-melding" />
                </h2>
            </VisibleIf>
            <VisibleIf visibleIf={traader.data.length > 0}>
                <MeldingListe meldinger={ulesteTraader} overskrift="innboks.uleste" />
                <MeldingListe meldinger={lesteTraader} overskrift="innboks.leste" />
            </VisibleIf>
        </div>
    );
}

ListeVisning.propTypes = {
    traader: storeShape(traadShape).isRequired,
    routes: PT.array.isRequired,
    params: PT.object.isRequired,
    location: PT.object.isRequired
};

const mapStateToProps = ({ traader }) => ({ traader });

export default connect(mapStateToProps)(ListeVisning);
