var React = require('react/addons');

var ariahelpermap = {
    info: 'Informasjonsmelding',
    feil: 'Feilmelding',
    ok: 'Bekreftelsemelding'
}

var InfoBoks = React.createClass({
    getDefaultProps: function(){
        return {
            focusOnRender: false
        }
    },
    componentDidMount: function(){
        if (this.props.focusOnRender) {
            var $infoboks = $(this.refs.infoboks.getDOMNode());
            $infoboks.find(':tabbable').first().focus();
        }
    },
    render: function () {
        var innerHTML = !this.props.melding && this.props.children ?
            <div>{this.props.children}</div> :
            <div dangerouslySetInnerHTML={{__html: this.props.melding}}></div>;

        return (
            <div className={"info-boks " + this.props.type} aria-live="assertive" aria-atomic="true" role="alert" ref="infoboks">
                <p className="vekk">{ariahelpermap[this.props.type]}</p>
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