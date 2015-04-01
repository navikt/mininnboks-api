var React = require('react/addons');
var FeedbackReporter = require('./FeedbackReporter');

var FeedbackForm = React.createClass({
    getInitialState: function () {
        return {errors: []};
    },
    updateErrorMessages: function (errors) {
        this.setState({errors: errors});
    },
    componentWillMount: function () {
        this.feedbackReporter = new FeedbackReporter(this.updateErrorMessages);
    },
    isValid: function () {
        return this.feedbackReporter.numberOfErrors() === 0;
    },
    getFeedbackRef: function (ref) {
        return this.refs[ref] || {};
    },
    getFeedbackRefs: function () {
        return this.refs;
    },
    validate: function () {
        for (var i in this.refs) {
            var child = this.refs[i];
            if (child.hasOwnProperty("validate")) {
                child.validate();
            }
        }
    },
    render: function () {
        var errors = this.state.errors;
        var feedback = null;
        var childrenProps = {reporter: this.feedbackReporter};

        if (this.feedbackReporter.numberOfErrors() === 1) {
            childrenProps.showInline = true;
        } else if (this.feedbackReporter.numberOfErrors() > 1) {
            childrenProps.showInline = false;
            feedback =
                <div role="alert" aria-live="assertive" aria-atomic="true" className="feilmelding">
                    <ul className="feedbackPanel">
                        {errors.map(function (e) {
                            return <li className="feedbackPanelERROR"><span className="feedbackPanelERROR">{e}</span>
                            </li>;
                        })}
                    </ul>
                </div>
        }

        var elements = this.props.children.map(function (child) {
            var childProps = $.extend({}, childrenProps, {ref: child.props.feedbackref || generateRef()});
            return React.addons.cloneWithProps(child, childProps);
        }.bind(this));
        return (
            <form className={this.props.className}>
                {feedback}
                {elements}
            </form>
        );
    }
});

function generateRef() {
    return "ref-" + Math.random();
}

module.exports = FeedbackForm;