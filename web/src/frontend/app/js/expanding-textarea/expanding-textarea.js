import React, { PropTypes as PT } from 'react';
import { injectIntl, FormattedMessage } from 'react-intl';
import classNames from 'classnames';
import { reduxFormProps } from './../utils/utils';

function ExpandingTextArea({ intl, makslengde, config, feilmeldingpanel }) {
    const resterendeLengde = makslengde - config.value.length;
    const showError = config.error && config.touched;

    const textareaClassname = classNames('input-fullbredde typo-normal', {
        invalid: showError
    });
    const ariadescribedby = showError ? intl.messages['textarea.feilmelding'] : '';
    const title = intl.messages['traadvisning.besvar.tekstfelt'];
    const ariaLabel = intl.messages['traadvisning.besvar.tekstfelt'];
    const feilmelding = showError ? (
        <span
            className="skjema-feilmelding"
            id="textarea.feilmelding"
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
        >
            <FormattedMessage id={`feilmelding.fritekst.${config.error}`} />
        </span>
    ) : null;

    /* eslint-disable jsx-a11y/no-onchange */
    return (
        <div className="textarea-meta-container js-container">
            <label htmlFor="fritekst">
                <span className="typo-normal max-length"><FormattedMessage id="textarea.infotekst" /></span>
            </label>
            {feilmeldingpanel}
            <textarea
                id="fritekst"
                name="fritekst"
                className={textareaClassname}
                autoFocus
                title={title}
                aria-label={ariaLabel}
                aria-invalid={showError}
                aria-describedby={ariadescribedby}
                {...reduxFormProps(config)}
            />
            <p className="textarea-metatekst" aria-hidden="true">
                <span className="max-length">{resterendeLengde}</span> tegn igjen
            </p>
            {feilmelding}
        </div>
    );
}

ExpandingTextArea.defaultProps = {
    makslengde: 1000,
    feilmeldingpanel: null
};
ExpandingTextArea.propTypes = {
    intl: PT.object.isRequired,
    config: PT.object.isRequired,
    makslengde: PT.number,
    feilmeldingpanel: PT.node
};

export default injectIntl(ExpandingTextArea);
