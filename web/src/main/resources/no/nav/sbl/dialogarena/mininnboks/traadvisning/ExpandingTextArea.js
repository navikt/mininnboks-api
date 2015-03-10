var React = require('react');

var ExpandingTextArea = React.createClass({
    getDefaultProps: function () {
        return {minAntallTegn: 1, maksAntallTegn: 1000, minHoydePx: 140, placeholder: ''}
    },
    getInitialState: function () {
        return {input: '', touched: false, validationMessages: []}
    },
    componentDidMount: function () {
        this.settPlaceholder();
        $(this.refs.textarea.getDOMNode()).focus();
    },
    getInput: function () {
        return this.state.input;
    },
    erValid: function () {
        return this.state.validationMessages.length === 0;
    },
    settPlaceholder: function () {
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
        var tekst = event.target.value;
        this.setState({input: tekst});
        this.justerTextAreaHoyde();
        this.valider(tekst);
    },
    onTextAreaBlur: function (event) {
        this.valider(event.target.value);
    },
    valider: function (tekst) {
        var antallTegn = this.antallTegn(tekst);
        var validationMessages = [];
        if (antallTegn > this.props.maksAntallTegn) {
            validationMessages.push('Teksten er for lang')
        } else if (antallTegn < this.props.minAntallTegn) {
            validationMessages.push('Tekstfeltet er tomt');
        }
        this.setState({touched: true, validationMessages: validationMessages});
    },
    antallTegn: function (input) {
        return input === this.props.placeholder ? 0 : input.length;
    },
    tegnIgjen: function () {
        return this.props.maksAntallTegn - this.antallTegn(this.state.input);
    },
    justerTextAreaHoyde: function () {
        var $mirror = $(this.refs.textareamirror.getDOMNode());
        var $textarea = $(this.refs.textarea.getDOMNode());
        $mirror
            .outerWidth($textarea.outerWidth())
            .text($textarea.val() + '\n');
        $textarea
            .outerHeight(Math.max($mirror.outerHeight(), this.props.minHoydePx) + 'px');
    },
    render: function () {
        var ingenTegnIgjenClass = this.tegnIgjen() >= 0 ? '' : 'invalid';
        var validClass = this.erValid() ? '' : 'invalid';
        var validationMessages = this.state.validationMessages.map(function (validationMessage) {
            return (<span className="validation-message">{validationMessage}</span>);
        });

        return (
            <div className="expandingtextarea">
                <div ref="textareamirror" className="textareamirror" aria-hidden="true"></div>
                <textarea ref="textarea" className={validClass}
                    aria-label={this.props.placeholder} aria-invalid={!this.erValid()} aria-describedby="validation-messages"
                    onChange={this.onTextAreaChange} onBlur={this.onTextAreaBlur} />
                <div id="validation-messages">
                    {validationMessages}
                </div>
                <span className={'tegnIgjen ' + ingenTegnIgjenClass}>{this.tegnIgjen()}</span>
                <span> Tegn igjen</span>
            </div>
        )
    }
});

module.exports = ExpandingTextArea;