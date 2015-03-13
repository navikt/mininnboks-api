var React = require('react');
var resources = require('resources');
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
        var knapper = <Snurrepipp storrelse="48" farge="graa" />;

        if (!this.state.sender) {
            knapper = (
                <div>
                    <input type="button" className="knapp-hoved-liten" value={resources.get('traadvisning.besvar.send')} onClick={this.onSubmit} />
                    <a href="#" onClick={this.skjul}>{resources.get('traadvisning.besvar.avbryt')}</a>
                </div>
            );
        }

        if (!this.props.vis) {
            return null;
        }

        return (
            <div className="besvar-container">
                <ExpandingTextArea placeholder={resources.get('traadvisning.besvar.tekstfelt.placeholder')} charsLeftText={resources.get('traadvisning.besvar.tekstfelt.tegnigjen')} ref="textarea" />
                {knapper}
            </div>
        );
    }
});

module.exports = BesvarBoks;