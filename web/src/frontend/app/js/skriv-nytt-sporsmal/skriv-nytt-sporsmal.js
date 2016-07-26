import React, { PropTypes as PT } from 'react';
import { createForm } from './../utils/nav-form/nav-form';
import { reduxForm } from 'redux-form';
import { bindActionCreators } from 'redux';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import GodtaVilkar from './godta-vilkar';
import Kvittering from './kvittering';
import Feilmelding from '../feilmelding/feilmelding';
import SendingStatus from './sending-status';
import InfoBoks from '../infoboks/infoboks';
import { sendSporsmal, velgVisModal } from '../utils/actions/actions';
import { FormattedMessage, injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from './../utils/brodsmulesti/custom-breadcrumbs';
import SamletFeilmeldingPanel from './samlet-feilmelding-panel';
import { validate } from './../validation/validationutil';

function SkrivNyttSporsmal({
    params, routes, actions, fields, errors, handleSubmit, submitFailed, submitToken,
    sendingStatus, visModal, godkjenteTemagrupper
}) {
    const temagruppe = params.temagruppe;

    const submit = (event) => {
        handleSubmit(({ fritekst }) => actions.sendSporsmal(temagruppe, fritekst))(event)
            .catch(() => {
                document.querySelector('.panel-feilsammendrag').focus();
            });
    };


    if (!godkjenteTemagrupper.includes(temagruppe)) {
        return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon />;
    } else if (sendingStatus === SendingStatus.ok) {
        return <Kvittering />;
    }

    const feilmeldingpanel = <SamletFeilmeldingPanel errors={errors} submitFailed={submitFailed} submitToken={submitToken} />;

    return (
        <form onSubmit={submit}>
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
                <ExpandingTextArea config={fields.fritekst} feilmeldingpanel={feilmeldingpanel}/>
                <GodtaVilkar
                    visModal={visModal}
                    config={fields.godkjennVilkaar}
                    actions={actions}
                />
                <button type="submit" className="knapp knapp-hoved knapp-stor">
                    <FormattedMessage id="send-sporsmal.still-sporsmal.send-inn" />
                </button>
            </article>
        </form>
    );
}

SkrivNyttSporsmal.propTypes = {
    params: PT.shape({
        temagruppe: PT.string
    }).isRequired,
    routes: PT.array.isRequired,
    actions: PT.shape({
        sendSporsmal: PT.func,
        velgVisModal: PT.func
    }).isRequired,
    fields: PT.object.isRequired,
    handleSubmit: PT.func.isRequired,
    sendingStatus: PT.string,
    visModal: PT.bool.isRequired,
    godkjenteTemagrupper: PT.arrayOf(PT.string).isRequired
};

const mapStateToProps = ({ data, form }) => ({
    submitToken: form['nytt-sporsmal'].submitToken,
    visModal: data.visModal,
    godkjenteTemagrupper: data.godkjenteTemagrupper,
    sendingStatus: data.sendingStatus
});
const mapDispatchToProps = (dispatch) => ({
    actions: bindActionCreators(
        { sendSporsmal, velgVisModal },
        dispatch
    )
});
const formConfig = {
    form: 'nytt-sporsmal',
    fields: ['fritekst', 'godkjennVilkaar'],
    returnRejectedSubmitPromise: true,
    validate
};

export default connect(mapStateToProps, mapDispatchToProps)(createForm(SkrivNyttSporsmal, formConfig, { fritekst: '', godkjennVilkaar: false }));

