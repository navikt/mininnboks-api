var React = require('react');

var ExpandingTextArea = React.createClass({
    getDefaultProps: function () {
        return {minAntallTegn: 1, maksAntallTegn: 1000, minHoydePx: 140, placeholder: ''}
    },
    getInitialState: function () {
        return {input: '', touched: false}
    },
    componentDidMount: function () {
        this.settPlaceholder();
        $(this.refs.expandingtextarea.getDOMNode()).focus();
    },
    getInput: function () {
        return this.state.input;
    },
    erValid: function () {
        if (this.state.touched) {
            var antallTegn = this.antallTegn();
            return antallTegn <= this.props.maksAntallTegn && antallTegn >= this.props.minAntallTegn;
        }
        return true;
    },
    settPlaceholder: function () {
        var placeholder = this.props.placeholder;
        if (placeholder.length === 0) {
            return;
        }

        $(this.refs.expandingtextarea.getDOMNode())
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
        this.setState({input: event.target.value});
        this.touch();
        this.justerTextAreaHoyde();
    },
    touch: function () {
        this.setState({touched: true})
    },
    antallTegn: function () {
        return this.state.input === this.props.placeholder ? 0 : this.state.input.length;
    },
    tegnIgjen: function () {
        return this.props.maksAntallTegn - this.antallTegn();
    },
    justerTextAreaHoyde: function () {
        var $mirror = $(this.refs.textareamirror.getDOMNode());
        var $textarea = $(this.refs.expandingtextarea.getDOMNode());
        $mirror
            .outerWidth($textarea.outerWidth())
            .text($textarea.val() + '\n');
        $textarea
            .outerHeight(Math.max($mirror.outerHeight(), this.props.minHoydePx) + 'px');
    },
    render: function () {
        var ingenTegnIgjenClass = this.tegnIgjen() >= 0 ? '' : 'invalid';
        var validClass = this.erValid() ? '' : 'invalid';

        return (
            <div>
                <div ref="textareamirror" className="textareamirror" aria-hidden="true"></div>
                <textarea ref="expandingtextarea" className={'expandingtextarea ' + validClass} onChange={this.onTextAreaChange} onBlur={this.touch}></textarea>
                <div>
                    <span className={'tegnIgjen ' + ingenTegnIgjenClass}>{this.tegnIgjen()}</span>
                    <span> Tegn igjen</span>
                </div>
            </div>
        )
    }
});

module.exports = ExpandingTextArea;