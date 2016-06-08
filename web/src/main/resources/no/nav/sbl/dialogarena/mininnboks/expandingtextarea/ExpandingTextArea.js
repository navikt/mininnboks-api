import React, { PropTypes as pt } from 'react';
import { connect } from 'react-redux';
import { skrivTekst } from '../utils/actions/actions.js';
import { injectIntl } from 'react-intl';
import SamletFeilmeldingPanel from './SamletFeilmeldingPanel';
import FeilmeldingEnum from '../skriv/FeilmeldingEnum';

class ExpandingTextArea extends React.Component {

    render() {
        const { formatMessage, dispatch, fritekst, validationResult } = this.props;
        const resterendeLengde = 1000 - fritekst.length;
        const hasValidationError = validationResult.includes(FeilmeldingEnum.textarea);
        const additionalClassName = hasValidationError ? 'invalid' : '';
        const feilmelding = hasValidationError
            ? <span className="skjema-feilmelding" id="textarea.feilmelding" role="alert" aria-live="assertive" aria-atomic="true">{formatMessage({ id: 'feilmeldingliste.textarea'}) }</span>
            : <noscript/>;
        const ariadescribedby = hasValidationError ?  'textarea.feilmelding' : '';

        return (
            <div className="textarea-meta-container js-container">
                <label for="textarea-med-meta">
                    <span className="typo-normal max-length">{formatMessage({ id: 'textarea.infotekst' })}</span>
                </label>
                <SamletFeilmeldingPanel formatMessage={formatMessage} validationResult={validationResult}/>

                <textarea id="textarea-med-meta" name="textarea-med-meta" className={`input-fullbredde typo-normal ${additionalClassName}`}
                          autofocus
                          title = { formatMessage({ id: 'traadvisning.besvar.tekstfelt' }) }
                          aria-label = { formatMessage({ id: 'traadvisning.besvar.tekstfelt' }) }
                          aria-invalid = { hasValidationError }
                          aria-describedby = { ariadescribedby }
                          onChange = { _onWrite(dispatch)}
                          value = { fritekst }
                />
                <p className="textarea-metatekst" aria-hidden="true">
                 <span class="max-length">{resterendeLengde}</span> tegn igjen
                </p>
                {feilmelding}
            </div>
        );
    }
}

const _onWrite = (dispatch) => (event) => dispatch(skrivTekst(event.target.value));

ExpandingTextArea.propTypes = {
    dispatch: pt.func,
    fritekst: pt.string.isRequired,
    validationResult: pt.array.isRequired
};

export default connect()(ExpandingTextArea);
