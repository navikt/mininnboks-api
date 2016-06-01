import React, { PropTypes as pt } from 'react';
import Betingelser from './Betingelser';
import { connect } from 'react-redux';
import { injectIntl } from 'react-intl';
import { velgGodtaVilkaar, velgVisModal } from './../utils/actions/actions';

const lukkModal = (dispatch) => () => dispatch(velgVisModal(false));

const godkjennVilkaar = (dispatch) => () => {
    dispatch(velgGodtaVilkaar(true));
    dispatch(velgVisModal(false));
};

const avbryt = (dispath) => () => {
    dispath(velgGodtaVilkaar(false));
    dispath(velgVisModal(false));
};

const toggleGodkjentVilkaar = (dispatch, alleredeValgt) => () => dispatch(velgGodtaVilkaar(!alleredeValgt));
const toggleVilkaarModal = (dispatch, alleredeValgt) => () => dispatch(velgVisModal(!alleredeValgt));

class GodtaVilkar extends React.Component {

    constructor(props) {
        super(props);
        this.visFeilmelding = false;
        this.validate = this.validate.bind(this);
    }

    onChange(event) {
        this.validate();
    }

    getErrorMessages() {
        return this.props.reporter.get(this.props.id);
    }

    validate() {
        if (this.props.godkjentVilkaar) {
            this.props.reporter.ok(this.props.id);
        } else {
            this.props.reporter.error(
                this.props.id,
                this.props.formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.feilmelding.ikke-akseptert' })
            );
        }
    }

    render() {
        const { formatMessage, godkjentVilkaar, dispatch, visModal } = this.props;
        // const validationFeilmelding = <span className="checkbox-feilmelding">{formatMessage({ id: 'godtavilkaar.validering.feilmelding' })}</span>;
        // const validationMessages = this.visFeilmelding && !godkjentVilkaar ? validationFeilmelding : <noscript/>;


        const errorMessages = this.getErrorMessages();
        const validationErrorClass = errorMessages.length === 0 ? '' : 'validation-error error';
        const validationMessages = this.props.showInline ? this.getErrorElements('span.checkbox-feilmelding', '-inline') : null;


        // this.visFeilmelding = true;
        const isOpen = visModal;

        const ariaCheckboxState = godkjentVilkaar ? 'Checkbox avkrysset' : 'Checkbox ikke avkrysset';

        return (
            <div className="betingelsevalgpanel">
                <div className="nav-input">
                    <span className="vekk" role="alert" aria-live="assertive" aria-atomic="true">{ariaCheckboxState}</span>
                    <input type="checkbox" name="betingelseValg:betingelserCheckbox" className="nav-checkbox" id="betingelser"
                      onChange={toggleGodkjentVilkaar(dispatch, godkjentVilkaar)} onBlur={this.validate} checked={godkjentVilkaar}
                           aria-invalid={!this.isValid()} aria-describedby={this.getErrorElementId('-inline')}
                    />
                    <label htmlFor="betingelser" className={`checkboxgruppe ${validationErrorClass}`}>
                        <span className="typo-infotekst">{formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.sjekkboks' })}</span>
                        <a href="#" className="typo-infotekst" onClick={toggleVilkaarModal(dispatch, isOpen)}>
                            {formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.vis' })}
                        </a>
                    </label>
                    <Betingelser ref="betingelser-panel" formatMessage={formatMessage} visModal={visModal}
                      godkjennVilkaar={godkjennVilkaar(dispatch)} avbryt={avbryt(dispatch)}
                      lukkModal={lukkModal(dispatch)}
                    />
                    {validationMessages}
                </div>
            </div>
        );
    }
}


GodtaVilkar.propTypes = {
    formatMessage: pt.func.isRequired,
    visModal: pt.bool.isRequired,
    id: pt.string,
    showInline: pt.bool,
    godkjentVilkaar: pt.bool.isRequired,
    dispatch: pt.func.isRequired
};

export default injectIntl(connect(({ godkjentVilkaar, visModal }) => ({ godkjentVilkaar, visModal }))(GodtaVilkar));
