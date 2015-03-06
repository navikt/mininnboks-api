var React = require('react');

var Knapper = React.createClass({
    render: function () {
        var info =
            <div className="info-boks">
                <p>Denne samtalen er avsluttet og du kan ikke svare på meldingen. Hvis du vil stille et nytt spørsmål kan du starte en <a href="#">ny tråd</a></p>
            </div>;
        return (
            <div className="knapper">
                <a href="/mininnboks/reactinnboks" className="knapp-liten">Innboks</a>
                <a href="#" className={'knapp-' + (this.props.kanBesvares ? 'hoved' : 'deaktivert') + '-liten'}>Skriv svar</a>
                {this.props.kanBesvares ? null : info}
            </div>
        )
    }
});

module.exports = Knapper;