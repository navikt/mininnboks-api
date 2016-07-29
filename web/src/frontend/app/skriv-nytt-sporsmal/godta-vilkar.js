import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import Betingelser from './betingelser';
import { reduxFormProps } from './../utils/utils';
import InlineFeilmelding from './../utils/nav-form/inline-feilmelding';

function GodtaVilkar({ visModal, actions, config }) {
    const ariaCheckboxState = config.checked ? 'Checkbox avkrysset' : 'Checkbox ikke avkrysset';

    const apneModal = () => actions.velgVisModal(true);
    const lukkModal = () => actions.velgVisModal(false);

    const godkjennVilkaar = () => {
        config.onChange(true);
        actions.velgVisModal(false);
    };

    const avbryt = () => {
        config.onChange(false);
        actions.velgVisModal(false);
    };

    const skalViseFeilmelding = !!(config.error && config.touched);

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
                        className="nav-checkbox " id="godkjennVilkaar"
                        aria-describedby="checkbox-feilmelding"
                        {...reduxFormProps(config)}
                    />
                    <label htmlFor="godkjennVilkaar">
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
                        visModal={visModal}
                        godkjennVilkaar={godkjennVilkaar}
                        avbryt={avbryt}
                        lukkModal={lukkModal}
                        name="betingelser-panel"
                    />
                    <InlineFeilmelding id="checkbox-feilmelding" visibleIf={skalViseFeilmelding}>
                        <FormattedMessage id="feilmelding.godkjennVilkaar.required" />
                    </InlineFeilmelding>
                </div>
            </div>
        </div>
    );
}

GodtaVilkar.propTypes = {
    visModal: PT.bool.isRequired,
    config: PT.object.isRequired,
    actions: PT.shape({
        velgVisModal: PT.func.isRequired
    }).isRequired
};

export default GodtaVilkar;
