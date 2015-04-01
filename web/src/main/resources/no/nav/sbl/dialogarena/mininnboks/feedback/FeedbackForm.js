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
    render: function () {
        var errors = this.state.errors;
        var feedback = null;
        var childrenProps = {reporter: this.feedbackReporter};

        if (this.feedbackReporter.numberOfErrors() === 1) {
            childrenProps.showInline = true;
        } else {
            childrenProps.showInline = false;
            feedback = errors.map(function (e) {
                return <p>{e}</p>;
            });
        }

        var elements = this.props.children.map(function (child) {
            var childProps = $.extend({}, childrenProps, {ref: child.props.feedbackref});
            return React.addons.cloneWithProps(child, childProps);
        }.bind(this));
        return (
            <form className={this.props.className}>
                {elements}
                <div>{feedback}</div>
            </form>
        );
    }
});

module.exports = FeedbackForm;