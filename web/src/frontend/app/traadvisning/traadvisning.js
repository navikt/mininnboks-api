import React, { PropTypes as PT } from 'react';
import BesvarBoks from './besvar-boks';
import Feilmelding from './../feilmelding/feilmelding';
import MeldingContainer from './melding-container';
import SkrivKnapp from './skriv-knapp';
import { STATUS } from './../ducks/utils';
import { FormattedMessage, FormattedHTMLMessage } from 'react-intl';
import Infopanel from './../infopanel/infopanel';
import { markerTraadSomLest, sendSvar, selectTraaderMedSammenslatteMeldinger } from './../ducks/traader';
import { visBesvarBoks, skjulBesvarBoks } from './../ducks/ui';
import { connect } from 'react-redux';
import { storeShape, traadShape } from './../proptype-shapes';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';
import { Sidetittel } from 'nav-react-design/dist/tittel';

const resolver = (temagruppe) => (key, tekst) => {
    if (key === ':tema') {
        return `Dialog om ${temagruppe}`;
    }
    return tekst;
};

class TraadVisning extends React.Component {
    componentDidMount() {
        this.props.actions.markerSomLest(this.props.params.traadId);
    }

    render() {
        const {
            routes, params, innsendingStatus, traader, skalViseBesvarBoks, actions
        } = this.props;

        const traadId = params.traadId;
        const valgttraad = traader.data.find(traad => traad.traadId === traadId);

        if (!valgttraad) {
            return (
                <Feilmelding tittel="Oops">
                    <p>Fant ikke tråden du var ute etter</p>
                </Feilmelding>
            );
        }


        const meldingItems = valgttraad.meldinger.map((melding) => (
            <MeldingContainer key={melding.id} melding={melding} />
        ));

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} resolver={resolver(valgttraad.nyeste.temagruppeNavn)} />
                <Sidetittel className="text-center blokk-l">
                    <FormattedMessage
                        id="traadvisning.overskrift"
                        values={{
                            kassert: valgttraad.nyeste.kassert,
                            temagruppeNavn: valgttraad.nyeste.temagruppeNavn
                        }}
                    />
                </Sidetittel>

                <div className="traad-container">
                    <SkrivKnapp
                        visibleIf={valgttraad.kanBesvares && !skalViseBesvarBoks}
                        onClick={actions.visBesvarBoks}
                    />
                    <Infopanel type="standard" visibleIf={valgttraad.avsluttet} horisontal>
                        <FormattedMessage id="skriv.ny.link">{(lenke) => (
                            <FormattedHTMLMessage id="traadvisning.kan-ikke-svare" values={{ lenke }} />
                        )}</FormattedMessage>
                    </Infopanel>
                    <Infopanel
                        type="advarsel"
                        visibleIf={innsendingStatus && innsendingStatus === STATUS.ERROR}
                        horisontal
                    >
                        <FormattedMessage id={'infoboks.advarsel'} />
                    </Infopanel>
                    <BesvarBoks
                        innsendingStatus={innsendingStatus}
                        visibleIf={skalViseBesvarBoks}
                        traadId={traadId}
                        avbryt={actions.skjulBesvarBoks}
                        submit={actions.sendSvar}
                    />
                    {meldingItems}
                </div>
            </div>
        );
    }
}

TraadVisning.propTypes = {
    traader: storeShape(traadShape).isRequired,
    skalViseBesvarBoks: PT.bool.isRequired,
    innsendingStatus: PT.string.isRequired,
    actions: PT.shape({
        sendSvar: PT.func.isRequired,
        markerSomLest: PT.func.isRequired,
        visBesvarBoks: PT.func.isRequired,
        skjulBesvarBoks: PT.func.isRequired
    }).isRequired,
    params: PT.object.isRequired,
    routes: PT.array.isRequired
};

const mapStateToProps = (state) => ({
    traader: selectTraaderMedSammenslatteMeldinger(state),
    innsendingStatus: state.traader.innsendingStatus,
    skalViseBesvarBoks: state.ui.visBesvarBoks
});

const mapDispatchToProps = (dispatch) => ({
    actions: {
        markerSomLest: (traadId) => dispatch(markerTraadSomLest(traadId)),
        visBesvarBoks: () => dispatch(visBesvarBoks()),
        skjulBesvarBoks: () => dispatch(skjulBesvarBoks()),
        sendSvar: (traadId, fritekst) => dispatch(sendSvar(traadId, fritekst))
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(TraadVisning);
