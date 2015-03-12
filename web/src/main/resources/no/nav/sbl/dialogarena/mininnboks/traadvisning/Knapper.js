var React = require('react');
var resources = require('resources');
var format = require('string-format');

var Knapper = React.createClass({
    besvar: function (event) {
        event.preventDefault();
        if (this.props.kanBesvares) {
            this.props.besvar();
        }
    },
    render: function () {
        var btnCls = format('knapp-{}-liten', this.props.kanBesvares && !this.props.besvares ? 'hoved' : 'deaktivert');

        return (
            <div className="knapper">
                <a href="/mininnboks" className="knapp-liten">{resources.get('traadvisning.innboks.link')}</a>
                <button onClick={this.besvar} className={btnCls}>{resources.get('traadvisning.skriv.svar.link')}</button>
            </div>
        )
    }
});

module.exports = Knapper;