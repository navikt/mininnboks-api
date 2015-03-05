var React = require('react');
var Melding = require('../innboks/Melding');

var MeldingContainer = React.createClass({
    render: function() {
        var melding = this.props.melding;
        var className = 'melding-container ' + (melding.fraBruker ? 'fra-bruker' : 'fra-nav');
        var imgSrc = melding.fraBruker ? '/mininnboks/img/personligoppmote.svg' : '/mininnboks/img/nav-logo.svg';
        return (
            <div className={className}>
                <img src={imgSrc} />
                <Melding melding={melding} />
            </div>
        );
    }
});

module.exports = MeldingContainer;