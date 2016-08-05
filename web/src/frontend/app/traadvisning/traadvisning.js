import React, { PropTypes as PT } from 'react';
import BesvarBoks from './besvar-boks';
import Feilmelding from './../feilmelding/feilmelding';
import MeldingContainer from './melding-container';
import SkrivKnapp from './skriv-knapp';
import { STATUS } from './../ducks/utils';
import { FormattedMessage, FormattedHTMLMessage } from 'react-intl';
import Infopanel from './../infopanel/infopanel';
import IntlLenke from './../utils/intl-lenke';
import { markerTraadSomLest, sendSvar } from './../ducks/traader';
import { visBesvarBoks, skjulBesvarBoks } from './../ducks/ui';
import { connect } from 'react-redux';
import { storeShape, traadShape } from './../proptype-shapes';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';

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
                        visibleIf={valgttraad.kanBesvares && !skalViseBesvarBoks}
                        onClick={actions.visBesvarBoks}
                    />
                    <Infopanel type="standard" visibleIf={!valgttraad.kanBesvares} horisontal>
                        <FormattedHTMLMessage id="traadvisning.kan-ikke-svare" />
                        <IntlLenke href="skriv.ny.link">
                            <FormattedMessage id="traadvisning.kan-ikke-svare.lenke" />
                        </IntlLenke>
                    </Infopanel>
                    <Infopanel
                        type="advarsel"
                        visibleIf={innsendingStatus && innsendingStatus === STATUS.ERROR}
                        horisontal
                    >
                        <FormattedMessage id={`infoboks.advarsel`} />
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

const mapStateToProps = ({ traader, ui }) => (
    { traader, innsendingStatus: traader.innsendingStatus, skalViseBesvarBoks: ui.visBesvarBoks }
);
const mapDispatchToProps = (dispatch) => ({
    actions: {
        markerSomLest: (traadId) => dispatch(markerTraadSomLest(traadId)),
        visBesvarBoks: () => dispatch(visBesvarBoks()),
        skjulBesvarBoks: () => dispatch(skjulBesvarBoks()),
        sendSvar: (traadId, fritekst) => dispatch(sendSvar(traadId, fritekst))
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(TraadVisning);
