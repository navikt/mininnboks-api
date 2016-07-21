import React, { PropTypes as PT } from 'react';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import GodtaVilkar from './godta-vilkar';
import Kvittering from './kvittering';
import Feilmelding from '../feilmelding/feilmelding';
import SendingStatus from './sending-status';
import InfoBoks from '../infoboks/infoboks';
import { resetInputState, sendSporsmal, submitSkjema } from '../utils/actions/actions';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/custom-breadcrumbs';
import { validate, getValidationMessages } from '../validation/validationutil';

const submit = (dispatch, temagruppe, fritekst, godkjentVilkaar) => () => {
    dispatch(submitSkjema(true));
    if (validate(true, fritekst, godkjentVilkaar)) {
        dispatch(sendSporsmal(temagruppe, fritekst));
    }
};

class Skriv extends React.Component {

    componentWillMount() {
        const { dispatch } = this.props;
        dispatch(resetInputState());
    }

    render() {
        const {
            params, dispatch, intl: { formatMessage }, visModal, routes,
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
            <form onSubmit={submit(dispatch, temagruppe, fritekst, godkjentVilkaar)}>
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
                    <ExpandingTextArea fritekst={fritekst} validationResult={validationResult} />
                    <GodtaVilkar
                        visModal={visModal}
                        validationResult={validationResult}
                        godkjentVilkaar={godkjentVilkaar}
                    />
                    <button type="submit" className="knapp knapp-hoved knapp-stor">
                        <FormattedMessage id="send-sporsmal.still-sporsmal.send-inn" />
                    </button>
                </article>
            </form>
        );
    }
}

Skriv.propTypes = {
    dispatch: PT.func,
    params: PT.object.isRequired,
    routes: PT.array.isRequired,
    fritekst: PT.string.isRequired,
    temagruppe: PT.string,
    intl: intlShape.isRequired,
    visModal: PT.bool.isRequired,
    harSubmittedSkjema: PT.bool.isRequired,
    sendingStatus: PT.string.isRequired,
    godkjentVilkaar: PT.bool.isRequired,
    validationResult: PT.array.isRequired
};

const mapStateToProps = ({ visModal, fritekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus }) => ({
    visModal,
    fritekst,
    harSubmittedSkjema,
    godkjentVilkaar,
    sendingStatus
});

export default injectIntl(connect(mapStateToProps)(Skriv));
