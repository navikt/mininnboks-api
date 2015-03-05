var React = require('react');

var AntallMeldinger = React.createClass({
    render: function() {
        var antall = this.props.antall;
        if (antall == 1) {
            return (
                <div className="ikon antall-en"></div>
            )
        } else {
            return (
                <div className="ikon antall-flere">
                    {antall}
                </div>
            )
        }
    }
});

module.exports = AntallMeldinger;