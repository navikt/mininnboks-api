import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import Betingelser from './betingelser';
import classNames from 'classnames';
import { reduxFormProps } from './../utils/utils';

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

    const feilmeldingKlasse = classNames('skjema-feilmelding', {
        vekk: !(config.touched && config.error)
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
                        className="nav-checkbox " id="godkjennVilkaar"
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
                    <span className={feilmeldingKlasse}>
                        <FormattedMessage id="feilmelding.godkjennVilkaar.required" />
                    </span>
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
