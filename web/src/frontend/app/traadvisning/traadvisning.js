import React, { PropTypes as PT } from 'react';
import BesvarBoks from './besvar-boks';
import MeldingContainer from './melding-container';
import SkrivKnapp from './skriv-knapp';
import { FormattedMessage } from 'react-intl';
import Infopanel from './../infopanel/infopanel';
import { lesTraad, resetInputState, settSkrivSvar, sendSvar } from '../utils/actions/actions';
import { connect } from 'react-redux';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';

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
            routes, params, sendingStatus, traader, skrivSvar, actions
        } = this.props;

        const traadId = params.traadId;
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
                        visibleIf={valgttraad.kanBesvares && skrivSvar}
                        onClick={actions.settSkrivSvar}
                    />
                    <Infopanel
                        type={sendingStatus}
                        visibleIf={sendingStatus && sendingStatus !== 'IKKE_SENDT'}
                        horisontal
                    >
                        <FormattedMessage id={`infoboks.${sendingStatus}`} />
                    </Infopanel>
                    <BesvarBoks
                        skrivSvar={skrivSvar}
                        traadId={traadId}
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
    skrivSvar: PT.bool.isRequired,
    sendingStatus: PT.string.isRequired,
    actions: PT.shape({
        resetInputState: PT.func.isRequired,
        sendSvar: PT.func.isRequired,
        lesTraad: PT.func.isRequired,
        settSkrivSvar: PT.func.isRequired
    }).isRequired,
    params: PT.object.isRequired,
    routes: PT.array.isRequired
};

const mapStateToProps = ({ data: { traader, skrivSvar, fritekst, sendingStatus } }) => (
    { traader, skrivSvar, fritekst, sendingStatus }
);
const mapDispatchToProps = (dispatch) => ({
    actions: {
        resetInputState: () => dispatch(resetInputState()),
        lesTraad: (traadId) => dispatch(lesTraad(traadId)),
        settSkrivSvar: () => dispatch(settSkrivSvar(true)),
        sendSvar: (traadId, fritekst) => dispatch(sendSvar(traadId, fritekst))
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(TraadVisning);
