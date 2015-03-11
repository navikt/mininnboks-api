var React = require('react');

var Feilmelding = React.createClass({
    componentWillMount: function(){
        if (!this.props.melding) {
            throw "Feilmelding komponenten må ha en en definert props kalt 'melding'";
        }
    },
    render: function () {
        return (
            <section className="feilmelding">
                <div className="robust-ikon-feil-gra"></div>
                <div className="stor">
                    {this.props.melding}
                </div>
            </section>
        );
    }
});


module.exports = Feilmelding;