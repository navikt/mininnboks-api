import React, { PropTypes as pt } from 'react';
import Betingelser from './Betingelser';
import { connect } from 'react-redux';
import { injectIntl } from 'react-intl';
import FeilmeldingEnum from './FeilmeldingEnum';
import { velgGodtaVilkaar, velgVisModal } from '../utils/actions/actions';

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

    render() {
        const { formatMessage, godkjentVilkaar, dispatch, visModal, validationResult } = this.props;
        const ariaCheckboxState = godkjentVilkaar ? 'Checkbox avkrysset' : 'Checkbox ikke avkrysset';

        const additionalClassName = validationResult.includes(FeilmeldingEnum.checkbox) ? '' : 'vekk';

        return (
            <div className="betingelsevalgpanel">
                <div className="nav-input">
                    <div className="checkboxgruppe">
                        <span className="vekk" role="alert" aria-live="assertive" aria-atomic="true">{ariaCheckboxState}</span>
                        <input type="checkbox" name="betingelseValg:betingelserCheckbox" className="nav-checkbox " id="betingelser"
                           onChange={toggleGodkjentVilkaar(dispatch, godkjentVilkaar)} onBlur={this.validate} checked={godkjentVilkaar}
                        />
                        <label htmlFor="betingelser">
                            <span className="typo-infotekst">{formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.sjekkboks' })}</span>
                            <a href="#" className="typo-infotekst" onClick={toggleVilkaarModal(dispatch, visModal)}>
                                {formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.vis' })}
                            </a>
                        </label>
                        <Betingelser ref="betingelser-panel" formatMessage={formatMessage} visModal={visModal}
                          godkjennVilkaar={godkjennVilkaar(dispatch)} avbryt={avbryt(dispatch)}
                          lukkModal={lukkModal(dispatch)} name="betingelser-panel"
                        />
                        <span className={`skjema-feilmelding ${additionalClassName}`}>{formatMessage({ id: 'godtavilkaar.validering.feilmelding' })}</span>
                    </div>
                </div>
            </div>
        );
    }
}


GodtaVilkar.propTypes = {
    formatMessage: pt.func.isRequired,
    visModal: pt.bool.isRequired,
    validationResult: pt.array.isRequired,
    dispatch: pt.func.isRequired
};

export default injectIntl(connect(({ }) => ({ }))(GodtaVilkar));
