var React = require('react/addons');
var ValidatableMixin = require('../feedback/ValidatableMixin');

var ExpandingTextArea = React.createClass({
    mixins: [ValidatableMixin],
    getDefaultProps: function () {
        return {minChars: 1, maxChars: 1000, minHeightPx: 140, placeholder: '', charsLeftText: 'Chars left'}
    },
    getInitialState: function () {
        return {input: ''}
    },
    componentDidMount: function () {
        this.setPlaceholder();
        $(this.refs.textarea.getDOMNode()).focus();
    },
    getInput: function () {
        return this.state.input;
    },
    setPlaceholder: function () {
        var placeholder = this.props.placeholder;
        if (placeholder.length === 0) {
            return;
        }
        $(this.refs.textarea.getDOMNode())
            .val(placeholder)
            .css('color', '#999')
            .focus(function () {
                if ($(this).val() === placeholder) {
                    $(this).val("");
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
    onTextAreaChange: function (event) {
        var text = event.target.value;
        this.setState({input: text});
        this.adjustTextAreaHeight();
        this.validate(text);
    },
    onTextAreaBlur: function (event) {
        this.validate(event.target.value);
    },
    validate: function (text) {
        text = text || this.state.input;
        var charCount = this.charCount(text);
        if (charCount > this.props.maxChars) {
            this.error('Teksten er for lang')
        } else if (charCount < this.props.minChars) {
            this.error('Tekstfeltet er tomt');
        } else {
            this.valid()
        }
    },
    charCount: function (input) {
        return input === this.props.placeholder ? 0 : input.length;
    },
    charsLeft: function () {
        return this.props.maxChars - this.charCount(this.state.input);
    },
    adjustTextAreaHeight: function () {
        var $mirror = $(this.refs.textareamirror.getDOMNode());
        var $textarea = $(this.refs.textarea.getDOMNode());
        $mirror
            .outerWidth($textarea.outerWidth())
            .text($textarea.val() + '\n');
        $textarea
            .outerHeight(Math.max($mirror.outerHeight(), this.props.minHeightPx) + 'px');
    },
    render: function () {
        var noMoreCharsClass = this.charsLeft() >= 0 ? '' : 'invalid';
        var textareaClass = this.isValid() ? '' : 'invalid';

        var validationMessages = this.props.showInline ? this.getErrorElements() : null;

        return (
            <div className="expandingtextarea">
                <div ref="textareamirror" className="textareamirror" aria-hidden="true"></div>
                <textarea ref="textarea" className={textareaClass}
                    aria-label={this.props.placeholder} aria-invalid={!this.isValid()} aria-describedby={this.getErrorElementId()}
                    onChange={this.onTextAreaChange} onBlur={this.onTextAreaBlur} />
                <span className={'charsLeft ' + noMoreCharsClass}>{this.charsLeft()}</span>
                <span>{' ' + this.props.charsLeftText}</span>
                <div id="validation-messages">
                    {validationMessages}
                </div>
            </div>
        )
    }
});

module.exports = ExpandingTextArea;