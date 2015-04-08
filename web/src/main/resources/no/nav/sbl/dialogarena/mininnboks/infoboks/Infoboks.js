var React = require('react/addons');

var InfoBoks = React.createClass({
    render: function () {
        var innerHTML = !this.props.melding && this.props.children ?
            <div>{this.props.children}</div> :
            <div dangerouslySetInnerHTML={{__html: this.props.melding}}></div>;

        return (
            <div className={"info-boks " + this.props.type}>
                {innerHTML}
            </div>
        );
    }
});

module.exports = {
    Info: React.createClass({
        render: function () {
            return <InfoBoks type="info" {...this.props} />;
        }
    }),
    Feil: React.createClass({
        render: function () {
            return <InfoBoks type="feil" {...this.props} />;
        }
    }),
    Ok: React.createClass({
        render: function () {
            return <InfoBoks type="ok" {...this.props} />;
        }
    })
};