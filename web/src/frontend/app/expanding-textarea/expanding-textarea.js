import React, { Component, PropTypes as PT } from 'react';
import { injectIntl, FormattedMessage } from 'react-intl';
import classNames from 'classnames';
import { reduxFormProps, autobind } from '../utils';
import InlineFeilmelding from './../utils/nav-form/inline-feilmelding';

class ExpandingTextArea extends Component {
    constructor(props) {
        super(props);
        autobind(this);
    }

    onChangeProxy(e) {
        this.refs.mirror.textContent = e.target.value;
        this.refs.textarea.style.height = `${this.refs.mirror.offsetHeight}px`;
        this.props.config.onChange(e);
    }

    render() {
        const { intl, makslengde, config } = this.props;
        const resterendeLengde = makslengde - config.value.length;
        const skalViseFeilmelding = !!(config.error && config.touched);

        const textareaClassname = classNames('textfelt input-fullbredde', { invalid: skalViseFeilmelding });
        const containerClassname = classNames('textarea-meta-container js-container', {
            'har-valideringsfeil': skalViseFeilmelding
        });

        const title = intl.formatMessage({ id: 'traadvisning.besvar.tekstfelt' });
        const ariaLabel = intl.formatMessage({ id: 'traadvisning.besvar.tekstfelt' });

        /* eslint-disable jsx-a11y/no-onchange */
        return (
            <div className={classNames('expanding-textarea', this.props.className)}>
                <div className="textareamirror" ref="mirror" aria-hidden="true"></div>
                <div className={containerClassname}>
                    <label htmlFor="fritekst">
                        <span className="typo-normal max-length vekk">
                            <FormattedMessage id="skriv.fritekst.label" values={{ antallTegn: makslengde }} />
                        </span>
                    </label>
                    <textarea
                        id="fritekst"
                        name="fritekst"
                        ref="textarea"
                        className={textareaClassname}
                        autoFocus
                        title={title}
                        aria-label={ariaLabel}
                        aria-invalid={skalViseFeilmelding}
                        aria-describedby="textarea.feilmelding"
                        {...reduxFormProps({ ...config, onChange: this.onChangeProxy })}
                    />
                    <p className="textarea-metatekst" aria-hidden="true">
                        <span className="max-length">{resterendeLengde}</span> tegn igjen
                    </p>
                </div>
                <InlineFeilmelding id="textarea.feilmelding" visibleIf={skalViseFeilmelding}>
                    <FormattedMessage id={`feilmelding.fritekst.${config.error}`} />
                </InlineFeilmelding>
            </div>
        );
    }
}

ExpandingTextArea.defaultProps = {
    makslengde: 1000
};
ExpandingTextArea.propTypes = {
    intl: PT.object.isRequired,
    config: PT.object.isRequired,
    makslengde: PT.number,
    className: PT.string
};

export default injectIntl(ExpandingTextArea);
