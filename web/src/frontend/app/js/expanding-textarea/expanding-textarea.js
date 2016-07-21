import React, { PropTypes as PT } from 'react';
import SamletFeilmeldingPanel from './samlet-feilmelding-panel';
import FeilmeldingEnum from '../skriv-nytt-sporsmal/feilmelding-enum';
import { injectIntl, FormattedMessage } from 'react-intl';
import classNames from 'classnames';

function ExpandingTextArea({ intl, fritekst, makslengde, validationResult, onChange }) {
    const resterendeLengde = makslengde - fritekst.length;
    const hasValidationError = validationResult.includes(FeilmeldingEnum.textarea);
    const textareaClassname = classNames('input-fullbredde typo-normal', {
        invalid: hasValidationError
    });
    const ariadescribedby = hasValidationError ? intl.messages['textarea.feilmelding'] : '';
    const title = intl.messages['traadvisning.besvar.tekstfelt'];
    const ariaLabel = intl.messages['traadvisning.besvar.tekstfelt'];
    const feilmelding = hasValidationError ? (
        <span
            className="skjema-feilmelding"
            id="textarea.feilmelding"
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
        >
            <FormattedMessage id="feilmeldingliste.textarea" />
        </span>
    ) : null;

    /* eslint-disable jsx-a11y/no-onchange */
    return (
        <div className="textarea-meta-container js-container">
            <label htmlFor="textarea-med-meta">
                <span className="typo-normal max-length"><FormattedMessage id="textarea.infotekst" /></span>
            </label>
            <SamletFeilmeldingPanel validationResult={validationResult} />
            <textarea
                id="textarea-med-meta"
                name="textarea-med-meta"
                className={textareaClassname}
                autoFocus
                title={title}
                aria-label={ariaLabel}
                aria-invalid={hasValidationError}
                aria-describedby={ariadescribedby}
                onChange={onChange}
                value={fritekst}
            />
            <p className="textarea-metatekst" aria-hidden="true">
                <span className="max-length">{resterendeLengde}</span> tegn igjen
            </p>
            {feilmelding}
        </div>
    );
}

ExpandingTextArea.defaultProps = {
    makslengde: 1000
};
ExpandingTextArea.propTypes = {
    intl: PT.object.isRequired,
    makslengde: PT.number,
    onChange: PT.func.isRequired,
    fritekst: PT.string.isRequired,
    validationResult: PT.array.isRequired
};

export default injectIntl(ExpandingTextArea);
