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
                <h1>
                    {this.props.melding}
                </h1>
            </section>
        );
    }
});


module.exports = Feilmelding;