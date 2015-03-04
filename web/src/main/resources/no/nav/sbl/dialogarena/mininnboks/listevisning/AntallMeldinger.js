var React = require('react');

var AntallMeldinger = React.createClass({
    render: function() {
        var antall = this.props.antall;
        if (antall == 2) {
            return (
                <div className="ikon antall-to"></div>
            )
        } else if (antall > 2) {
            return (
                <div className="ikon antall-flere">
                    {antall}
                </div>
            )
        } else return null;
    }
});

module.exports = AntallMeldinger;