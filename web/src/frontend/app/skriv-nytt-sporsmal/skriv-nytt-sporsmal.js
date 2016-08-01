import React, { PropTypes as PT } from 'react';
import { createForm } from './../utils/nav-form/nav-form';
import { bindActionCreators } from 'redux';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import GodtaVilkar from './godta-vilkar';
import Kvittering from './kvittering';
import Feilmelding from '../feilmelding/feilmelding';
import SendingStatus from './sending-status';
import Infopanel from '../infopanel/infopanel';
import { sendSporsmal, velgVisModal } from '../utils/actions/actions';
import { FormattedMessage } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';
import SamletFeilmeldingPanel from '../utils/nav-form/samlet-feilmelding-panel';
import { validate } from '../utils/validationutil';

const ukjentTemagruppeTittel = <FormattedMessage id="skriv-sporsmal.ukjent-temagruppe"/>;

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
        return (
            <Feilmelding tittel={ukjentTemagruppeTittel} />
        );
    } else if (sendingStatus === SendingStatus.ok) {
        return <Kvittering />;
    }

    return (
        <article className="blokk-center send-sporsmal-side">
            <Breadcrumbs routes={routes} params={params} className="blokk-s" />
            <h1 className="typo-sidetittel text-center blokk-m">
                <FormattedMessage id="send-sporsmal.still-sporsmal.ny-melding-overskrift" />
            </h1>
            <form className="panel text-center" onSubmit={submit}>
                <h2 className="hode hode-innholdstittel hode-dekorert meldingikon">
                    <FormattedMessage id="send-sporsmal.still-sporsmal.deloverskrift" />
                </h2>
                <p className="text-bold blokk-null"><FormattedMessage id={temagruppe} /></p>
                <Infopanel type={sendingStatus} visibleIf={sendingStatus && sendingStatus !== 'IKKE_SENDT'} horisontal>
                    <FormattedMessage id={`infoboks.${sendingStatus}`} />
                </Infopanel>
                <p className="typo-normal blokk-xs"><FormattedMessage id="textarea.infotekst" /></p>
                <SamletFeilmeldingPanel errors={errors} submitFailed={submitFailed} submitToken={submitToken} />
                <ExpandingTextArea config={fields.fritekst} className="blokk-m" />
                <GodtaVilkar
                    visModal={visModal}
                    config={fields.godkjennVilkaar}
                    actions={actions}
                />
                <button type="submit" className="knapp knapp-hoved knapp-stor">
                    <FormattedMessage id="send-sporsmal.still-sporsmal.send-inn" />
                </button>
            </form>
        </article>
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
    errors: PT.object.isRequired,
    fields: PT.object.isRequired,
    handleSubmit: PT.func.isRequired,
    submitFailed: PT.bool.isRequired,
    submitToken: PT.string,
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

export default connect(mapStateToProps, mapDispatchToProps)(
    createForm(SkrivNyttSporsmal, formConfig, { fritekst: '', godkjennVilkaar: false })
);
