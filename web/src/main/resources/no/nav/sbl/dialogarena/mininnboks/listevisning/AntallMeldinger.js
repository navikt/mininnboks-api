var React = require('react');

var AntallMeldinger = React.createClass({
    render: function () {
        var antall = this.props.antall;
        var antallCls = antall === 1 ? 'ikon antall-en' : 'ikon antall-flere';
        var antallTekst = antall === 1 ? null : antall;

        return (
            <div className={antallCls}>{antallTekst}</div>
        );
    }
});

module.exports = AntallMeldinger;