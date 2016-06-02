import React from 'react';
import ValidatableMixin from '../feedback/ValidatableMixin';

const ExpandingTextArea = React.createClass({
    mixins: [ValidatableMixin],
    getDefaultProps() {
        return { minChars: 1, maxChars: 1000, minHeightPx: 140, placeholder: '', charsLeftText: 'Chars left' };
    },

    getInitialState() {
        return { input: '' };
    },

    componentDidMount() {
        this.setPlaceholder();
        $(this.refs.textarea).focus();
    },

    onTextAreaChange(event) {
        const text = event.target.value;
        this.setState({ input: text });
        this.adjustTextAreaHeight();
        this.validate(text);
    },

    onTextAreaBlur(event) {
        this.validate(event.target.value);
    },

    getInput() {
        return this.state.input;
    },

    setPlaceholder() {
        const placeholder = this.props.placeholder;
        if (placeholder.length === 0) {
            return;
        }
        $(this.refs.textarea)
            .val(placeholder)
            .css('color', '#999')
            .focus(function () {
                if ($(this).val() === placeholder) {
                    $(this).val('');
                    $(this).css('color', 'inherit');
                }
            })
            .blur(function () {
                if ($(this).val().length === 0) {
                    $(this).val(placeholder);
                    $(this).css('color', '#999');
                }
            });
    },

    validate(text) {
        text = text || this.state.input;
        const charCount = this.charCount(text);
        if (charCount > this.props.maxChars) {
            this.error('Teksten er for lang');
        } else if (charCount < this.props.minChars) {
            this.error('Tekstfeltet er tomt');
        } else {
            this.valid();
        }
    },

    charCount(input) {
        return input === this.props.placeholder ? 0 : input.length;
    },

    charsLeft() {
        return this.props.maxChars - this.charCount(this.state.input);
    },

    adjustTextAreaHeight() {
        const $mirror = $(this.refs.textareamirror);
        const $textarea = $(this.refs.textarea);
        $mirror
            .outerWidth($textarea.outerWidth())
            .text(` ${$textarea.val()} \n`);
        $textarea
            .outerHeight(` ${Math.max($mirror.outerHeight(), this.props.minHeightPx)} px`);
    },

    render() {
        const { infotekst } = this.props;
        const textareaClass = this.isValid() ? '' : 'invalid';
        const validationMessages = this.props.showInline ? this.getErrorElements(undefined, '-inline') : null;

        return (
            <div className="textarea-meta-container js-container">
                <label for="textarea-med-meta">
                    <span className="typo-normal max-length">{infotekst}</span>
                </label>
                <textarea id="textarea-med-meta" name="textarea-med-meta" className={`input-fullbredde typo-normal ${textareaClass}`}
                  title={this.props.placeholder}
                  aria-label={this.props.placeholder} aria-invalid={!this.isValid()} aria-describedby={this.getErrorElementId('-inline')}
                  onChange={this.onTextAreaChange} onBlur={this.onTextAreaBlur}
                />
                <p className="textarea-metatekst" aria-hidden="true">
                 <span class="max-length">{this.charsLeft()}</span> tegn igjen
                </p>
                <div id="validation-messages">
                    {validationMessages}
                </div>
            </div>
        );
    }
});

export default ExpandingTextArea;
