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
import { addXsrfHeader } from '../utils/Utils';
import { resetInputState, sendSporsmal, submitSkjema, settSendingStatus } from '../utils/actions/actions';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/customBreadcrumbs';
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
        const { params, dispatch, intl: { formatMessage }, visModal, routes, fritekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus } = this.props;
        const temagruppe = this.props.params.temagruppe;
        if (formatMessage({id: 'temagruppe.liste'}).split(' ').indexOf(temagruppe) < 0) {
            return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon/>;
        } else if (sendingStatus == SendingStatus.ok) {
            return <Kvittering formatMessage={formatMessage}/>;
        }
        const validationResult = getValidationMessages(harSubmittedSkjema, fritekst, godkjentVilkaar);

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} formatMessage={formatMessage}/>
                <h1 className="typo-sidetittel text-center blokk-l">{formatMessage({id: 'send-sporsmal.still-sporsmal.ny-melding-overskrift'})}</h1>
                <article className="send-sporsmal-container send-panel">
                    <div className="sporsmal-header">
                        <h2 className="hode hode-innholdstittel hode-dekorert meldingikon">{formatMessage({id: 'send-sporsmal.still-sporsmal.deloverskrift'})}</h2>
                    </div>
                    <strong>{formatMessage({id: temagruppe})}</strong>
                    <InfoBoks sendingStatus={sendingStatus}/>
                    <ExpandingTextArea formatMessage={formatMessage} fritekst={fritekst}
                                       validationResult={validationResult}/>
                    <GodtaVilkar formatMessage={formatMessage} visModal={visModal} validationResult={validationResult}
                                 godkjentVilkaar={godkjentVilkaar}/>
                    <input type="submit" className="knapp knapp-hoved knapp-stor" role="button"
                           value={formatMessage({ id: 'send-sporsmal.still-sporsmal.send-inn' })}
                           onClick={submit(dispatch, temagruppe, fritekst, godkjentVilkaar)}
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

const mapStateToProps = ({ visModal, fritekst, harSubmittedSkjema, godkjentVilkaar, sendingStatus  }) => ({
    visModal,
    fritekst,
    harSubmittedSkjema,
    godkjentVilkaar,
    sendingStatus
});

export default injectIntl(connect(mapStateToProps)(Skriv));
