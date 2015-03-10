var React = require('react');
var ExpandingTextArea = require('./ExpandingTextArea');

var BesvarBoks = React.createClass({
    onSubmit: function () {
        var textarea = this.refs.textarea;
        if (textarea.isValid()) {
            this.props.besvar(textarea.getInput());
        }
    },
    skjul: function (event) {
        event.preventDefault();
        this.props.skjul();
    },
    render: function () {
        return this.props.vis ?
            (<div className="besvar-container">
                <ExpandingTextArea placeholder="Skriv et svar" ref="textarea" />
                <input type="button" className="knapp-hoved-liten" value="Send svar" onClick={this.onSubmit} />
                <a href="#" onClick={this.skjul}>Avbryt</a>
            </div>) : null;
    }
});

module.exports = BesvarBoks;