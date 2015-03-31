var React = require('react');
var ValidatableMixin = require('./ValidatableMixin');

var Test = React.createClass({
    mixins: [ValidatableMixin],
    validate: function (event) {
        var text = event.target.value;
        if (text.length === 0) {
            this.error('Was not valid...');
        } else {
            this.valid();
        }
    },
    render: function () {
        var errors = this.getErrorMessages();
        var inlineFeedback = this.props.showInline ? errors.map(function (e) {
            return <p>{e}</p>
        }) : null;
        return (
            <div>
                <input type="text" onBlur={this.validate} onChange={this.validate}/>
                {inlineFeedback}
            </div>
        );
    }
});

module.exports = Test;