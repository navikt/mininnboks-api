import React from 'react/addons';

var AntallMeldinger = React.createClass({
    render: function () {
        var antall = this.props.antall;
        var antallCls = antall === 1 ? 'antall-ikon antall-en' : 'antall-ikon antall-flere';
        var antallTekst = antall === 1 ? null : antall;

        return (
            <div className={antallCls} aria-hidden="true">{antallTekst}</div>
        );
    }
});

module.exports = AntallMeldinger;