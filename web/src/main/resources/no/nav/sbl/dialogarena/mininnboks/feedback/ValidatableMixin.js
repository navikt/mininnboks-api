var React = require('react/addons');

module.exports = {
    getInitialState: function () {
        if (!this.props.hasOwnProperty('id')) {
            throw new TypeError(this.constructor.displayName + " uses the ValidatableMixin, but has not been given an id. If it used within a FeedbackForm you should give it a 'feedbackref' props");
        }
        return {
            uuid: this.props.id
        };
    },
    getDefaultProps: function () {
        return {showInline: false};
    },
    valid: function () {
        this.props.reporter.ok(this.state.uuid);
    },
    error: function (msgs) {
        this.props.reporter.error(this.state.uuid, msgs);
    },
    getErrorMessages: function () {
        return this.props.reporter.get(this.state.uuid);
    },
    isValid: function(){
        return this.getErrorMessages().length === 0;
    },
    getErrorElements: function (tagConfig, idSuffix) {
        return this.props.reporter.getErrorElementsForComponent(this.state.uuid, tagConfig, idSuffix);
    },
    getErrorElementId: function (idSuffix) {
        return this.props.reporter.getErrorMessageIdForComponent(this.state.uuid, idSuffix);
    }
};