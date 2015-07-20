var React = require('react/addons');
var Melding = require('../melding/Melding');

var MeldingContainer = React.createClass({
    render: function() {
        var melding = this.props.melding;
        var className = 'melding-container ' + (melding.fraBruker ? 'fra-bruker' : 'fra-nav');
        var imgSrc = melding.fraBruker ? '/mininnboks/build/img/personikon.svg' : '/mininnboks/build/img/nav-logo.svg';
        var imgTekstKey = melding.fraBruker ? 'innboks.avsender.bruker' : 'innboks.avsender.nav';

        return (
            <div className={className}>
                <div className="logo" aria-label={this.props.resources.get(imgTekstKey)}>
                    <img src={imgSrc} alt={this.props.resources.get(imgTekstKey)} />
                </div>
                <Melding melding={melding} lagLenkerAvURL={true} />
            </div>
        );
    }
});

module.exports = MeldingContainer;