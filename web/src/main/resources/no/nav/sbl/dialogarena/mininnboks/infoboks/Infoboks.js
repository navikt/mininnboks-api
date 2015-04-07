var React = require('react/addons');

var InfoBoks = React.createClass({
    render: function () {
        var innerHTML = !this.props.melding && this.props.children ?
            <div>{this.props.children}</div> :
            <div dangerouslySetInnerHTML={{__html: this.props.melding}}></div>;

        return (
            <div className="info-boks">
                {innerHTML}
            </div>
        );
    }
});

module.exports = InfoBoks;