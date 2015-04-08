var React = require('react/addons');
var ExpandingTextArea = require('../expandingtextarea/ExpandingTextArea');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var FeedbackForm = require('../feedback/FeedbackForm');


var BesvarBoks = React.createClass({
    getInitialState: function () {
        return {sender: false}
    },
    onSubmit: function () {
        var form = this.refs.form;
        if (form.isValid()) {
            this.props.besvar(form.getFeedbackRef('textarea').getInput());
            this.setState({sender: true});
        }
    },
    skjul: function (event) {
        event.preventDefault();
        this.props.skjul();
    },
    render: function () {
        if (!this.props.vis) {
            return null;
        }

        var knapper;
        if (this.state.sender) {
            knapper = <Snurrepipp storrelse="48" farge="graa" />;
        } else {
            knapper = (
                <div>
                    <input type="button" className="knapp-hoved-liten" value={this.props.resources.get('traadvisning.besvar.send')} onClick={this.onSubmit} />
                    <a href="#" onClick={this.skjul} role="button">{this.props.resources.get('traadvisning.besvar.avbryt')}</a>
                </div>
            );
        }

        return (
            <div aria-atomic="true" aria-live="polite">
                <FeedbackForm className="besvar-container" ref="form">
                    <ExpandingTextArea placeholder={this.props.resources.get('traadvisning.besvar.tekstfelt.placeholder')} charsLeftText={this.props.resources.get('traadvisning.besvar.tekstfelt.tegnigjen')} feedbackref="textarea" />
                {knapper}
                </FeedbackForm>
            </div>
        );
    }
});

module.exports = BesvarBoks;