var React = require('react');
var Resources = require('resources');

var Knapper = React.createClass({
    besvar: function (event) {
        event.preventDefault();
        if (this.props.kanBesvares) {
            this.props.besvar();
        }
    },
    render: function () {
        var info =
            <div className="info-boks" dangerouslySetInnerHTML={{__html: Resources.get('traadvisning.kan.ikke.svare.info')}}></div>;

        return (
            <div className="knapper">
                <a href="/mininnboks" className="knapp-liten">Innboks</a>
                <a href="#"
                    onClick={this.besvar}
                    className={'knapp-' + (this.props.kanBesvares && !this.props.besvares ? 'hoved' : 'deaktivert') + '-liten'}>Skriv svar</a>
                {this.props.kanBesvares ? null : info}
            </div>
        )
    }
});

module.exports = Knapper;