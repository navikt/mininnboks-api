var React = require('react');
var Melding = require('melding');

var MeldingContainer = React.createClass({
    render: function() {
        var melding = this.props.melding;
        var className = 'melding-container ' + (melding.fraBruker ? 'fra-bruker' : 'fra-nav');
        var imgSrc = melding.fraBruker ? '/mininnboks/img/personikon.svg' : '/mininnboks/img/nav-logo.svg';
        return (
            <div className={className}>
                <div className="logo"><img src={imgSrc} /></div>
                <Melding melding={melding} lagLenkerAvURL={true} />
            </div>
        );
    }
});

module.exports = MeldingContainer;