var React = require('react');

var Feilmelding = React.createClass({
    componentWillMount: function () {
        if (!this.props.melding) {
            throw "Feilmelding komponenten må ha en en definert props kalt 'melding'";
        }
    },
    render: function () {
        var ikon = this.props.visIkon ? <div className="robust-ikon-feil-gra"></div> : null;

        return (
            <section className="feilmelding">
                {ikon}
                <h1>
                    {this.props.melding}
                </h1>
                <span>{this.props.brodtekst}</span>
            </section>
        );
    }
});


module.exports = Feilmelding;