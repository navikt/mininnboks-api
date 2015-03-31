module.exports = {
    getInitialState: function () {
        return {
            randomId: Math.random()
        };
    },
    getDefaultProps: function () {
        return {showInline: false};
    },
    valid: function() {
        this.props.reporter.ok(this.state.randomId);
    },
    error: function(msgs) {
        this.props.reporter.error(this.state.randomId, msgs);
    },
    getErrorMessages: function() {
        return this.props.reporter.get(this.state.randomId);
    }
};