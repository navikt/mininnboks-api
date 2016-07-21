import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import Betingelser from './betingelser';
import FeilmeldingEnum from './feilmelding-enum';
import classNames from 'classnames';

function GodtaVilkar() {
    const { godkjentVilkaar, visModal, validationResult, actions } = this.props;
    const ariaCheckboxState = godkjentVilkaar ? 'Checkbox avkrysset' : 'Checkbox ikke avkrysset';

    const apneModal = () => actions.velgVisModal(true);
    const lukkModal = () => actions.velgVisModal(false);

    const godkjennVilkaar = () => {
        actions.velgGodtaVilkaar(true);
        actions.velgVisModal(false);
    };

    const avbryt = () => {
        actions.velgGodtaVilkaar(false);
        actions.velgVisModal(false);
    };

    const toggleGodkjentVilkaar = (alleredeValgt) => () => actions.velgGodtaVilkaar(!alleredeValgt);

    const feilmeldingKlasse = classNames('skjema-feilmelding', {
        vekk: validationResult.includes(FeilmeldingEnum.checkbox)
    });

    /* eslint-disable jsx-a11y/no-onchange, no-script-url */
    return (
        <div className="betingelsevalgpanel">
            <div className="nav-input">
                <div className="checkboxgruppe">
                    <span className="vekk" role="alert" aria-live="assertive" aria-atomic="true">
                        {ariaCheckboxState}
                    </span>
                    <input
                        type="checkbox"
                        name="betingelseValg:betingelserCheckbox"
                        className="nav-checkbox " id="betingelser"
                        onChange={toggleGodkjentVilkaar(godkjentVilkaar)}
                        checked={godkjentVilkaar}
                    />
                    <label htmlFor="betingelser">
                        <span className="typo-infotekst">
                            <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.sjekkboks" />
                        </span>
                        <a
                            href="javascript:void(0)"
                            className="typo-infotekst"
                            onClick={apneModal}
                        >
                            <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.vis" />
                        </a>
                    </label>
                    <Betingelser
                        ref="betingelser-panel"
                        visModal={visModal}
                        godkjennVilkaar={godkjennVilkaar}
                        avbryt={avbryt}
                        lukkModal={lukkModal}
                        name="betingelser-panel"
                    />
                    <span className={feilmeldingKlasse}>
                        <FormattedMessage id="godtavilkaar.validering.feilmelding" />
                    </span>
                </div>
            </div>
        </div>
    );
}

GodtaVilkar.propTypes = {
    visModal: PT.bool.isRequired,
    godkjentVilkaar: PT.bool.isRequired,
    validationResult: PT.array.isRequired,
    actions: PT.shape({
        velgVisModal: PT.func.isRequired,
        velgGodtaVilkaar: PT.func.isRequired
    }).isRequired
};

export default GodtaVilkar;
