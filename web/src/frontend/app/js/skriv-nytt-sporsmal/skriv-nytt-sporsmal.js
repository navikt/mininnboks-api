import React, { PropTypes as PT } from 'react';
import { bindActionCreators } from 'redux';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import GodtaVilkar from './godta-vilkar';
import Kvittering from './kvittering';
import Feilmelding from '../feilmelding/feilmelding';
import SendingStatus from './sending-status';
import InfoBoks from '../infoboks/infoboks';
import { resetInputState, sendSporsmal, skrivTekst, submitSkjema, velgVisModal, velgGodtaVilkaar } from '../utils/actions/actions';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/custom-breadcrumbs';
import { validate, getValidationMessages } from '../validation/validationutil';

const submit = (actions, temagruppe, fritekst, godkjentVilkaar) => (event) => {
    event.preventDefault();

    actions.submitSkjema(true);
    if (validate(true, fritekst, godkjentVilkaar)) {
        actions.sendSporsmal(temagruppe, fritekst);
    }
};

class SkrivNyttSporsmal extends React.Component {

    componentWillMount() {
        this.props.actions.resetInputState();
    }

    render() {
        const {
            params, intl: { formatMessage }, visModal, routes, actions,
            fritekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus
        } = this.props;

        const temagruppe = this.props.params.temagruppe;
        if (formatMessage({ id: 'temagruppe.liste' }).split(' ').indexOf(temagruppe) < 0) {
            return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon />;
        } else if (sendingStatus === SendingStatus.ok) {
            return <Kvittering />;
        }
        const validationResult = getValidationMessages(harSubmittedSkjema, fritekst, godkjentVilkaar);

        return (
            <form onSubmit={submit(actions, temagruppe, fritekst, godkjentVilkaar)}>
                <Breadcrumbs routes={routes} params={params} />
                <h1 className="typo-sidetittel text-center blokk-l">
                    <FormattedMessage id="send-sporsmal.still-sporsmal.ny-melding-overskrift" />
                </h1>
                <article className="send-sporsmal-container send-panel">
                    <div className="sporsmal-header">
                        <h2 className="hode hode-innholdstittel hode-dekorert meldingikon">
                            <FormattedMessage id="send-sporsmal.still-sporsmal.deloverskrift" />
                        </h2>
                    </div>
                    <strong><FormattedMessage id={temagruppe} /></strong>
                    <InfoBoks sendingStatus={sendingStatus} />
                    <ExpandingTextArea
                        fritekst={fritekst}
                        validationResult={validationResult}
                        onChange={(e) => actions.skrivTekst(e.target.value)}
                    />
                    <GodtaVilkar
                        visModal={visModal}
                        validationResult={validationResult}
                        godkjentVilkaar={godkjentVilkaar}
                        actions={actions}
                    />
                    <button type="submit" className="knapp knapp-hoved knapp-stor">
                        <FormattedMessage id="send-sporsmal.still-sporsmal.send-inn" />
                    </button>
                </article>
            </form>
        );
    }
}

SkrivNyttSporsmal.propTypes = {
    dispatch: PT.func,
    params: PT.object.isRequired,
    routes: PT.array.isRequired,
    fritekst: PT.string.isRequired,
    temagruppe: PT.string,
    intl: intlShape.isRequired,
    visModal: PT.bool.isRequired,
    harSubmittedSkjema: PT.bool.isRequired,
    sendingStatus: PT.string.isRequired,
    godkjentVilkaar: PT.bool.isRequired
};

const mapStateToProps = ({ data: { visModal, fritekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus } }) => ({
    visModal,
    fritekst,
    harSubmittedSkjema,
    godkjentVilkaar,
    sendingStatus
});
const mapDispatchToProps = (dispatch) => ({
    actions: bindActionCreators(
        { resetInputState, sendSporsmal, submitSkjema, velgVisModal, velgGodtaVilkaar, skrivTekst },
        dispatch
    )
});

export default injectIntl(connect(mapStateToProps, mapDispatchToProps)(SkrivNyttSporsmal));
