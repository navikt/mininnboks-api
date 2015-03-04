var React = require('react');

var TraadVisning = React.createClass({
    render: function() {
        return (
            <h1>{this.props.id}</h1>
        );
    }
});

module.exports = TraadVisning;