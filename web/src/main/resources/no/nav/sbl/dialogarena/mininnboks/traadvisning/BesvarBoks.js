var React = require('react');
var ExpandingTextArea = require('./ExpandingTextArea');
var Snurrepipp = require('snurrepipp');


var BesvarBoks = React.createClass({
    getInitialState: function () {
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
        var knapper = <Snurrepipp storrelse="48" farge="hvit" />;

        if (!this.state.sender) {
            knapper = (
                <div>
                    <input type="button" className="knapp-hoved-liten" value="Send svar" onClick={this.onSubmit} />
                    <a href="#" onClick={this.skjul}>Avbryt</a>
                </div>
            );
        }

        if (!this.props.vis) {
            return null;
        }

        return (
            <div className="besvar-container">
                <ExpandingTextArea placeholder="Skriv et svar" ref="textarea" />
                {knapper}
            </div>
        );
    }
});

module.exports = BesvarBoks;