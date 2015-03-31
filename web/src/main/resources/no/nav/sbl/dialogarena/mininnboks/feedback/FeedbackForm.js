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
            return React.addons.cloneWithProps(child, childrenProps)
        }.bind(this));
        return (
            <form>
                {elements}
                <div>{feedback}</div>
            </form>
        );
    }
});

module.exports = FeedbackForm;