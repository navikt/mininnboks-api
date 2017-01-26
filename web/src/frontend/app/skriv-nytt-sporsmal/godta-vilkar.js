import React, { PropTypes as PT } from 'react';
import { FormattedMessage } from 'react-intl';
import Betingelser from './betingelser';
import { reduxFormProps } from '../utils';
import InlineFeilmelding from './../utils/nav-form/inline-feilmelding';

function GodtaVilkar({ visModal, actions, config }) {
    const ariaCheckboxState = config.checked ? 'Checkbox avkrysset' : 'Checkbox ikke avkrysset';

    const godkjennVilkaar = () => {
        config.onChange(true);
        actions.skjulVilkarModal();
    };

    const avbryt = () => {
        config.onChange(false);
        actions.skjulVilkarModal();
    };

    const skalViseFeilmelding = !!(config.error && config.touched);

    /* eslint-disable jsx-a11y/no-onchange, no-script-url */
    return (
        <div className="godtavilkaar-panel blokk-m">
            <div className="nav-input">
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
                <label htmlFor="godkjennVilkaar" className="betingelsevalg-checkbox">
                    <span className="typo-infotekst">
                        <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.sjekkboks" />
                    </span>
                    <a
                        href="javascript:void(0)"
                        className="typo-infotekst"
                        onClick={actions.visVilkarModal}
                    >
                        <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.vis" />
                    </a>
                </label>
                <Betingelser
                    visModal={visModal}
                    godkjennVilkaar={godkjennVilkaar}
                    avbryt={avbryt}
                    lukkModal={actions.skjulVilkarModal}
                    name="betingelser-panel"
                />
                <InlineFeilmelding id="checkbox-feilmelding" visibleIf={skalViseFeilmelding}>
                    <FormattedMessage id="feilmelding.godkjennVilkaar.required" />
                </InlineFeilmelding>
            </div>
        </div>
    );
}

GodtaVilkar.propTypes = {
    visModal: PT.bool.isRequired,
    config: PT.object.isRequired,
    actions: PT.shape({
        visVilkarModal: PT.func.isRequired,
        skjulVilkarModal: PT.func.isRequired
    }).isRequired
};

export default GodtaVilkar;
