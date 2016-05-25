import React from 'react';
import FeedbackReporter from './FeedbackReporter';

class FeedbackForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {errors: []};
        this.updateErrorMessages = this.updateErrorMessages.bind(this);
        this.isValid = this.isValid.bind(this);
        this.getFeedbackRef = this.getFeedbackRef.bind(this);
        this.getFeedbackRefs = this.getFeedbackRefs.bind(this);
        this.validate = this.validate.bind(this);
    }

    updateErrorMessages (errors) {
        this.setState({errors: errors});
    }

    componentWillMount () {
        this.feedbackReporter = new FeedbackReporter(this.updateErrorMessages);
    }

    isValid () {
        return this.feedbackReporter.numberOfErrors() === 0;
    }

    getFeedbackRef (ref) {
        return this.refs[ref] || {};
    }

    getFeedbackRefs () {
        return this.refs;
    }

    validate () {
        for (var i in this.refs) {
            var child = this.refs[i];
            if (child.hasOwnProperty("validate")) {
                child.validate();
            }
        }
    }

    render () {
        var feedback = null;
        var childrenProps = {reporter: this.feedbackReporter, showInline: true};

        if (this.feedbackReporter.numberOfErrors() > 1) {
            var errors = this.feedbackReporter.getAllErrorElements("li.feedbackPanelERROR");
            feedback =
                <div role="alert" aria-live="assertive" aria-atomic="true" className="feilmelding">
                    <ul className="feedbackPanel">
                        {errors}
                    </ul>
                </div>
        }

        var elements = this.props.children
            .filter(function (child) {
                return child ? true : false;
            })
            .map(function (child) {
                if (!child.props.hasOwnProperty('feedbackref')) {
                    return child;
                }
                var refId = child.props.feedbackref;
                var childProps = $.extend({}, childrenProps, {ref: refId, id: refId, key: refId});
                return cloneElement(child, childProps);
            }.bind(this));
        return (
            <form className={this.props.className}>
                {feedback}
                {elements}
            </form>
        );
    }
};

function generateRef() {
    return "ref-" + Math.random();
}

export default FeedbackForm;