var React = require('react');
var ExpandingTextArea = require('./ExpandingTextArea');

var BesvarBoks = React.createClass({
    onSubmit: function () {
        this.props.besvar(this.refs.textarea.getInput());
    },
    render: function () {
        return (
            <div className="besvarContainer">
                <ExpandingTextArea placeholder="Skriv et svar" ref="textarea" />
                <input type="button" className="knapp-hoved-liten" value="Send svar" onClick={this.onSubmit} />
            </div>
        )
    }
});

module.exports = BesvarBoks;