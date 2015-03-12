var React = require('react');

var InfoBoks = React.createClass({
    getDefaultProps: function () {
        return {melding: ''}
    },
    render: function () {
        return (
            this.props.melding ? <div className="info-boks" dangerouslySetInnerHTML={{__html: this.props.melding}}></div> : null
        )
    }
});

module.exports = InfoBoks;