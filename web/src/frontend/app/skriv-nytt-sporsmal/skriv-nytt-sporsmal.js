import React, { PropTypes as PT } from 'react';
import { createForm } from './../utils/nav-form/nav-form';
import { bindActionCreators } from 'redux';
import { visVilkarModal, skjulVilkarModal } from './../ducks/ui';
import { sendSporsmal } from './../ducks/traader';
import { STATUS } from './../ducks/utils';
import ExpandingTextArea from '../expanding-textarea/expanding-textarea';
import GodtaVilkar from './godta-vilkar';
import Kvittering from './kvittering';
import Feilmelding from '../feilmelding/feilmelding';
import Infopanel from '../infopanel/infopanel';
import { FormattedMessage } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';
import SamletFeilmeldingPanel from '../utils/nav-form/samlet-feilmelding-panel';
import { validate } from '../utils/validationutil';
import { Hovedknapp } from 'nav-react-design/dist/knapp';
import { Sidetittel } from 'nav-react-design/dist/tittel';

const ukjentTemagruppeTittel = <FormattedMessage id="skriv-sporsmal.ukjent-temagruppe" />;

function SkrivNyttSporsmal({
    params, routes, actions, fields, errors, handleSubmit, submitFailed, submitToken,
    sendingStatus, skalViseVilkarModal, godkjenteTemagrupper
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
    } else if (sendingStatus === STATUS.OK) {
        return <Kvittering />;
    }

    return (
        <article className="blokk-center send-sporsmal-side">
            <Breadcrumbs routes={routes} params={params} className="blokk-s" />
            <Sidetittel className="text-center blokk-m">
                <FormattedMessage id="send-sporsmal.still-sporsmal.ny-melding-overskrift" />
            </Sidetittel>
            <form className="panel text-center" onSubmit={submit}>
                <h2 className="hode hode-innholdstittel hode-dekorert meldingikon">
                    <FormattedMessage id="send-sporsmal.still-sporsmal.deloverskrift" />
                </h2>
                <p className="text-bold blokk-null"><FormattedMessage id={temagruppe} /></p>
                <Infopanel
                    type="advarsel"
                    visibleIf={sendingStatus && sendingStatus === STATUS.ERROR}
                    horisontal
                >
                    <FormattedMessage id="infoboks.advarsel" />
                </Infopanel>
                <p className="typo-normal blokk-xs"><FormattedMessage id="textarea.infotekst" /></p>
                <SamletFeilmeldingPanel errors={errors} submitFailed={submitFailed} submitToken={submitToken} />
                <ExpandingTextArea config={fields.fritekst} className="blokk-m" />
                <GodtaVilkar
                    visModal={skalViseVilkarModal}
                    config={fields.godkjennVilkaar}
                    actions={actions}
                />
                <Hovedknapp type="submit" spinner={sendingStatus === STATUS.PENDING}>
                    <FormattedMessage id="send-sporsmal.still-sporsmal.send-inn" />
                </Hovedknapp>
            </form>
        </article>
    );
}
SkrivNyttSporsmal.defaultProps = {
    godkjenteTemagrupper: ['ARBD']
};
SkrivNyttSporsmal.propTypes = {
    params: PT.shape({
        temagruppe: PT.string
    }).isRequired,
    routes: PT.array.isRequired,
    actions: PT.shape({
        sendSporsmal: PT.func,
        visVilkarModal: PT.func
    }).isRequired,
    errors: PT.object.isRequired,
    fields: PT.object.isRequired,
    handleSubmit: PT.func.isRequired,
    submitFailed: PT.bool.isRequired,
    submitToken: PT.string,
    sendingStatus: PT.string,
    skalViseVilkarModal: PT.bool.isRequired,
    godkjenteTemagrupper: PT.arrayOf(PT.string).isRequired
};

const mapStateToProps = ({ ledetekster, traader, ui, form }) => ({
    submitToken: form['nytt-sporsmal'].submitToken,
    skalViseVilkarModal: ui.visVilkarModal,
    godkjenteTemagrupper: ledetekster.godkjenteTemagrupper,
    sendingStatus: traader.innsendingStatus
});
const mapDispatchToProps = (dispatch) => ({
    actions: bindActionCreators(
        { sendSporsmal, visVilkarModal, skjulVilkarModal },
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

