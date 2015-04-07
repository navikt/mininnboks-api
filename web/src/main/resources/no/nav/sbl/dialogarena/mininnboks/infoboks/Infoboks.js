var React = require('react/addons');

var InfoBoks = React.createClass({
    getDefaultProps: function(){
        return {
            cls: 'info'
        };
    },
    render: function () {
        var innerHTML = !this.props.melding && this.props.children ?
            <div>{this.props.children}</div> :
            <div dangerouslySetInnerHTML={{__html: this.props.melding}}></div>;

        return (
            <div className={"info-boks " + this.props.cls}>
                {innerHTML}
            </div>
        );
    }
});

module.exports = {
    Info: React.createClass({render: function(){ return <InfoBoks cls="info" {...this.props} />;}}),
    Feil: React.createClass({render: function(){ return <InfoBoks cls="feil" {...this.props} />;}}),
    Ok: React.createClass({render: function(){ return <InfoBoks cls="ok" {...this.props} />;}})
};