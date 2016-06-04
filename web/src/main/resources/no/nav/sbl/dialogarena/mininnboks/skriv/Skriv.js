import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import GodtaVilkar from './GodtaVilkar';
import Kvittering from './Kvittering';
import Feilmelding from '../feilmelding/Feilmelding';
import SendingStatus from './SendingStatus';
import InfoBoks from '../infoboks/Infoboks';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import FeilmeldingEnum from './FeilmeldingEnum';
import Utils from '../utils/Utils';
import { submitSkjema, settSendingStatus } from '../utils/actions/actions.js';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/customBreadcrumbs';


const submit = (dispatch, temagruppe, sporsmalInputtekst, harSubmittedSkjema, godkjentVilkaar) => () => {
    dispatch(submitSkjema(true));
    if (validate(true, sporsmalInputtekst, godkjentVilkaar)) {
        $.ajax({
                type: 'POST',
                url: '/mininnboks/tjenester/traader/sporsmal',
                contentType: 'application/json',
                data: JSON.stringify({ temagruppe, sporsmalInputtekst }),
                beforeSend: Utils.addXsrfHeader
            })
            .done(function (response, status, xhr) {
                if (xhr.status !== 201) {
                    dispatch(settSendingStatus(SendingStatus.feil));
                } else {
                    dispatch(settSendingStatus(SendingStatus.ok));
                }
            }.bind(this))
            .fail(function () {
                dispatch(settSendingStatus(SendingStatus.feil));
            }.bind(this));
    }
};

const validateTextarea = (sporsmalInputtekst, harSubmittedSkjema) => {
    return !(sporsmalInputtekst.length === 0 && harSubmittedSkjema);
};

const validateCheckbox = (godkjentVilkaar, harSubmittedSkjema) => {
    return !(!godkjentVilkaar && harSubmittedSkjema);
};

const validate = (harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar) => {
    return getValidationMessages(harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar).length === 0;
};

export const getValidationMessages = (harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar) => {
    let validationMessages = [];
    if (!validateTextarea(sporsmalInputtekst, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.textarea);
    }
    if (!validateCheckbox(godkjentVilkaar, harSubmittedSkjema)) {
        validationMessages.push(FeilmeldingEnum.checkbox);
    }
    return validationMessages;
};

class Skriv extends React.Component {

    render() {
        const { params, dispatch, intl: { formatMessage }, visModal, routes, sporsmalInputtekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus } = this.props;
        const temagruppe = this.props.params.temagruppe;
        if (formatMessage({ id: 'temagruppe.liste' }).split(' ').indexOf(temagruppe) < 0) {
            return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon/>;
        } else if (sendingStatus == SendingStatus.ok) {
            return <Kvittering formatMessage={formatMessage}/>;
        }
        const validationResult = getValidationMessages(harSubmittedSkjema, sporsmalInputtekst, godkjentVilkaar);

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} formatMessage={formatMessage} />
                <h1 className="typo-sidetittel text-center blokk-l">{formatMessage({ id: 'send-sporsmal.still-sporsmal.ny-melding-overskrift' })}</h1>
                <article className="send-sporsmal-container send-panel">
                    <div className="sporsmal-header">
                        <h2 className="hode hode-innholdstittel hode-dekorert meldingikon">{formatMessage({ id: 'send-sporsmal.still-sporsmal.deloverskrift' })}</h2>
                    </div>
                    <strong>{formatMessage({ id: temagruppe })}</strong>
                    <InfoBoks formatMessage={formatMessage} sendingStatus={sendingStatus} />
                    <ExpandingTextArea formatMessage={formatMessage} sporsmalInputtekst={sporsmalInputtekst} validationResult={validationResult}/>
                    <GodtaVilkar formatMessage={formatMessage} visModal={visModal} validationResult={validationResult} godkjentVilkaar={godkjentVilkaar}/>
                    <input type="submit" className="knapp knapp-hoved knapp-stor" role="button"
                           value={formatMessage({ id: 'send-sporsmal.still-sporsmal.send-inn' })}
                           onClick={submit(dispatch, temagruppe, sporsmalInputtekst, harSubmittedSkjema, godkjentVilkaar)}
                    />
                </article>
            </div>
        );
    }
}

Skriv.propTypes = {
    temagruppe: pt.string,
    intl: intlShape.isRequired,
    visModal: pt.bool.isRequired,
    harSubmittedSkjema: pt.bool.isRequired,
    sendingStatus: pt.string.isRequired,
    godkjentVilkaar: pt.bool.isRequired,
    validationResult: pt.array.isRequired
};

const mapStateToProps = ({ visModal, sporsmalInputtekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus  }) => ({ visModal, sporsmalInputtekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus });

export default injectIntl(connect(mapStateToProps)(Skriv));
