import React, { PropTypes as PT } from 'react';
import BesvarBoks from'./besvar-boks';
import MeldingContainer from './melding-container';
import SkrivKnapp from './skriv-knapp';
import { FormattedMessage } from 'react-intl';
import InfoBoks from '../infoboks/infoboks';
import { lesTraad, resetInputState, skrivTekst, settSkrivSvar, sendSvar } from '../utils/actions/actions';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/custom-breadcrumbs';

const breadcrumbsresolver = (temagruppe) => (key, tekst) => {
    if (key === ':tema') {
        return `Dialog om ${temagruppe}`;
    }
    return tekst;
};

class TraadVisning extends React.Component {

    componentWillMount() {
        this.props.resetInputState();
    }

    componentDidMount() {
        this.props.lesTraad(this.props.params.traadId);
    }

    render() {
        const {
            routes, params, sendingStatus, traader, skrivSvar, skrivTekst,
            harSubmittedSkjema, fritekst, settSkrivSvar, resetInputState, sendSvar
        } = this.props;
        const traadId = this.props.params.traadId;
        const valgttraad = traader.find(traad => traad.traadId === traadId);

        const meldingItems = valgttraad.meldinger.map((melding) => (
            <MeldingContainer key={melding.id} melding={melding} />
        ));

        const overskriftKey = valgttraad.nyeste.kassert ? 'traadvisning.overskrift.kassert' : 'traadvisning.overskrift';
        const overskrift = <FormattedMessage id={overskriftKey} values={{ temagruppeNavn: valgttraad.nyeste.temagruppeNavn }} />

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} resolver={breadcrumbsresolver(valgttraad.nyeste.temagruppeNavn)} />
                <h1 className="typo-sidetittel text-center blokk-l">{overskrift}</h1>
                <div className="traad-container">
                    <SkrivKnapp kanBesvares={valgttraad.kanBesvares} skrivSvar={skrivSvar} onClick={settSkrivSvar} />
                    <InfoBoks sendingStatus={sendingStatus} />
                    <BesvarBoks 
                        fritekst={fritekst} 
                        skrivSvar={skrivSvar} 
                        harSubmittedSkjema={harSubmittedSkjema} 
                        traadId={traadId}
                        skrivTekst={skrivTekst}
                        avbryt={resetInputState}
                        submit={sendSvar}
                    />
                    {meldingItems}
                </div>
            </div>
        );
        
    }
}

TraadVisning.propTypes = {
    traader: PT.array.isRequired,
    harSubmittedSkjema: PT.bool.isRequired,
    skrivSvar: PT.bool.isRequired,
    fritekst: PT.string.isRequired,
    sendingStatus: PT.string.isRequired,
    resetInputState: PT.func.isRequired,
    sendSvar: PT.func.isRequired,
    skrivTekst: PT.func.isRequired,
    lesTraad: PT.func.isRequired
};

const mapStateToProps = ({ traader, harSubmittedSkjema, skrivSvar, fritekst, sendingStatus   }) => (
    { traader, harSubmittedSkjema, skrivSvar, fritekst, sendingStatus  }
);
const mapDispatchToProps = (dispatch) => ({
    skrivTekst: (tekst) => dispatch(skrivTekst(tekst)),
    resetInputState: () => dispatch(resetInputState()),
    lesTraad: (traadId) => dispatch(lesTraad(traadId)),
    settSkrivSvar: () => dispatch(settSkrivSvar(true)),
    sendSvar: (traadId, fritekst) => dispatch(sendSvar(traadId, fritekst))
});

export default connect(mapStateToProps, mapDispatchToProps)(TraadVisning);
