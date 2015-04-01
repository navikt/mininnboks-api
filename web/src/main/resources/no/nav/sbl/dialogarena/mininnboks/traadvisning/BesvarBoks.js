var React = require('react');
var Resources = require('../resources/Resources');
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
        var knapper = <Snurrepipp storrelse="48" farge="graa" />;

        if (!this.state.sender) {
            knapper = (
                <div>
                    <input type="button" className="knapp-hoved-liten" value={Resources.get('traadvisning.besvar.send')} onClick={this.onSubmit} />
                    <a href="#" onClick={this.skjul}>{Resources.get('traadvisning.besvar.avbryt')}</a>
                </div>
            );
        }

        if (!this.props.vis) {
            return null;
        }

        return (
            <FeedbackForm className="besvar-container" ref="form">
                <ExpandingTextArea placeholder={Resources.get('traadvisning.besvar.tekstfelt.placeholder')} charsLeftText={Resources.get('traadvisning.besvar.tekstfelt.tegnigjen')} feedbackref="textarea" />
                {knapper}
            </FeedbackForm>
        );
    }
});

module.exports = BesvarBoks;