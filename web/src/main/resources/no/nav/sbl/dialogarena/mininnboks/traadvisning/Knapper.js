var React = require('react');
var resources = require('resources');

var Knapper = React.createClass({
    besvar: function (event) {
        event.preventDefault();
        if (this.props.kanBesvares) {
            this.props.besvar();
        }
    },
    render: function () {
        var info =
            <div className="info-boks" dangerouslySetInnerHTML={{__html: resources.get('traadvisning.kan.ikke.svare.info')}}></div>;

        return (
            <div className="knapper">
                <a href="/mininnboks" className="knapp-liten">{resources.get('traadvisning.innboks.link')}</a>
                <a href="#"
                    onClick={this.besvar}
                    className={'knapp-' + (this.props.kanBesvares && !this.props.besvares ? 'hoved' : 'deaktivert') + '-liten'}>{resources.get('traadvisning.skriv.svar.link')}</a>
                {this.props.avsluttet ? info : null}
            </div>
        )
    }
});

module.exports = Knapper;