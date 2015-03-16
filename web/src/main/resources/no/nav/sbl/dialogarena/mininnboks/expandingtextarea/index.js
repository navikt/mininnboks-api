var React = require('react');

var ExpandingTextArea = React.createClass({
    getDefaultProps: function () {
        return {minChars: 1, maxChars: 1000, minHeightPx: 140, placeholder: '', charsLeftText: 'Chars left'}
    },
    getInitialState: function () {
        return {input: '', touched: false, validationMessages: []}
    },
    componentDidMount: function () {
        this.setPlaceholder();
        $(this.refs.textarea.getDOMNode()).focus();
    },
    getInput: function () {
        return this.state.input;
    },
    isValid: function () {
        return this.state.validationMessages.length === 0;
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
    validate: function (tekst) {
        var charCount = this.charCount(tekst);
        var validationMessages = [];
        if (charCount > this.props.maxChars) {
            validationMessages.push('Teksten er for lang')
        } else if (charCount < this.props.minChars) {
            validationMessages.push('Tekstfeltet er tomt');
        }
        this.setState({touched: true, validationMessages: validationMessages});
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
        var validClass = this.isValid() ? '' : 'invalid';
        var validationMessages = this.state.validationMessages.map(function (validationMessage) {
            return (<span className="validation-message">{validationMessage}</span>);
        });

        return (
            <div className="expandingtextarea">
                <div ref="textareamirror" className="textareamirror" aria-hidden="true"></div>
                <textarea ref="textarea" className={validClass}
                    aria-label={this.props.placeholder} aria-invalid={!this.isValid()} aria-describedby="validation-messages"
                    onChange={this.onTextAreaChange} onBlur={this.onTextAreaBlur} />
                <div id="validation-messages">
                    {validationMessages}
                </div>
                <span className={'charsLeft ' + noMoreCharsClass}>{this.charsLeft()}</span>
                <span>{' ' + this.props.charsLeftText}</span>
            </div>
        )
    }
});

module.exports = ExpandingTextArea;