var React = require('react');

var ExpandingTextArea = React.createClass({
    getDefaultProps: function () {
        return {maksAntallTegn: 1000, minHoydePx: 140, placeholder: ''}
    },
    getInitialState: function () {
        return {input: '', antallTegn: 0, valid: true}
    },
    componentDidMount: function () {
        this.settPlaceholder();
    },
    getInput: function () {
        return this.state.input;
    },
    erValid: function () {
        console.log(this.state.antallTegn);
        this.setState({valid: this.state.antallTegn <= this.props.maksAntallTegn && this.state.antallTegn !== 0});
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
    onTextAreaInput: function (event) {
        this.setState({input: event.target.value});
        this.sjekkAntallTegn(event);
        this.justerTextAreaHoyde();
        this.erValid();
    },
    tegnIgjen: function () {
        return this.props.maksAntallTegn - this.state.antallTegn;
    },
    sjekkAntallTegn: function (event) {
        var tekst = $(event.target).val();
        this.setState({antallTegn: tekst === this.props.placeholder ? 0 : tekst.length});
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
        var validClass = this.state.valid ? '' : 'invalid';

        return (
            <div>
                <div ref="textareamirror" className="textareamirror"></div>
                <textarea ref="expandingtextarea" className={'expandingtextarea ' + validClass} onInput={this.onTextAreaInput} onFocus={this.sjekkAntallTegn}></textarea>
                <div>
                    <span className={'tegnIgjen ' + ingenTegnIgjenClass}>{this.tegnIgjen()}</span>
                    <span> Tegn igjen</span>
                </div>
            </div>
        )
    }
});

module.exports = ExpandingTextArea;