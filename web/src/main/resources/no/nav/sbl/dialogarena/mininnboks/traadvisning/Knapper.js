var React = require('react');

var Knapper = React.createClass({
    render: function() {
        return (
            <div className="knapper">
                <a href="/mininnboks/reactinnboks" className="knapp-liten">Innboks</a>
                <a href="#" className={'knapp-' + (this.props.kanBesvares ? 'hoved' : 'deaktivert') + '-liten'}>Skriv svar</a>
            </div>
        )
    }
});

module.exports = Knapper;