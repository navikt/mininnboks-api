var React = require('react');
var ExpandingTextArea = require('./ExpandingTextArea');

var BesvarBoks = React.createClass({
    getInitialState: function() {
        return {sender: false}
    },
    onSubmit: function () {
        var textarea = this.refs.textarea;
        if (textarea.isValid()) {
            this.props.besvar(textarea.getInput());
            this.setState({sender: true});
        }
    },
    skjul: function (event) {
        event.preventDefault();
        this.props.skjul();
    },
    render: function () {
        var knapper = this.state.sender ? <img className="sender-spinner" src="/mininnboks/img/ajaxloader/hvit/loader_hvit_48.gif" /> :
            <div><input type="button" className="knapp-hoved-liten" value="Send svar" onClick={this.onSubmit} /><a href="#" onClick={this.skjul}>Avbryt</a></div>;

        return this.props.vis ?
            (<div className="besvar-container">
                <ExpandingTextArea placeholder="Skriv et svar" ref="textarea" />
                {knapper}
            </div>) : null;
    }
});

module.exports = BesvarBoks;