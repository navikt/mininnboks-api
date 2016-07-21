import React, { PropTypes as PT } from 'react';
import BesvarBoks from './besvar-boks';
import MeldingContainer from './melding-container';
import SkrivKnapp from './skriv-knapp';
import { FormattedMessage } from 'react-intl';
import InfoBoks from '../infoboks/infoboks';
import { lesTraad, resetInputState, skrivTekst, settSkrivSvar, sendSvar } from '../utils/actions/actions';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/custom-breadcrumbs';

const resolver = (temagruppe) => (key, tekst) => {
    if (key === ':tema') {
        return `Dialog om ${temagruppe}`;
    }
    return tekst;
};

class TraadVisning extends React.Component {

    componentWillMount() {
        this.props.actions.resetInputState();
    }

    componentDidMount() {
        this.props.actions.lesTraad(this.props.params.traadId);
    }

    render() {
        const {
            routes, params, sendingStatus, traader, skrivSvar, harSubmittedSkjema, fritekst, actions
        } = this.props;

        const traadId = this.props.params.traadId;
        const valgttraad = traader.find(traad => traad.traadId === traadId);

        const meldingItems = valgttraad.meldinger.map((melding) => (
            <MeldingContainer key={melding.id} melding={melding} />
        ));

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} resolver={resolver(valgttraad.nyeste.temagruppeNavn)} />
                <h1 className="typo-sidetittel text-center blokk-l">
                    <FormattedMessage
                        id="traadvisning.overskrift"
                        values={{
                            kassert: valgttraad.nyeste.kassert,
                            temagruppeNavn: valgttraad.nyeste.temagruppeNavn
                        }}
                    />
                </h1>
                <div className="traad-container">
                    <SkrivKnapp
                        kanBesvares={valgttraad.kanBesvares}
                        skrivSvar={skrivSvar}
                        onClick={actions.settSkrivSvar}
                    />
                    <InfoBoks sendingStatus={sendingStatus} />
                    <BesvarBoks
                        fritekst={fritekst}
                        skrivSvar={skrivSvar}
                        harSubmittedSkjema={harSubmittedSkjema}
                        traadId={traadId}
                        skrivTekst={actions.skrivTekst}
                        avbryt={actions.resetInputState}
                        submit={actions.sendSvar}
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
    actions: PT.shape({
        resetInputState: PT.func.isRequired,
        sendSvar: PT.func.isRequired,
        skrivTekst: PT.func.isRequired,
        lesTraad: PT.func.isRequired,
        settSkrivSvar: PT.func.isRequired
    }).isRequired,
    params: PT.object.isRequired,
    routes: PT.array.isRequired
};

const mapStateToProps = ({ traader, harSubmittedSkjema, skrivSvar, fritekst, sendingStatus }) => (
    { traader, harSubmittedSkjema, skrivSvar, fritekst, sendingStatus }
);
const mapDispatchToProps = (dispatch) => ({
    actions: {
        skrivTekst: (tekst) => dispatch(skrivTekst(tekst)),
        resetInputState: () => dispatch(resetInputState()),
        lesTraad: (traadId) => dispatch(lesTraad(traadId)),
        settSkrivSvar: () => dispatch(settSkrivSvar(true)),
        sendSvar: (traadId, fritekst) => dispatch(sendSvar(traadId, fritekst))
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(TraadVisning);
