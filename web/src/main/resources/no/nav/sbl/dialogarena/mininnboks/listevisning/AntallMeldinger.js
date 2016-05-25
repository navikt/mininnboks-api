import React from 'react';

class AntallMeldinger extends React.Component {
    render () {
        var antall = this.props.antall;
        var antallCls = antall === 1 ? 'antall-ikon antall-en' : 'antall-ikon antall-flere';
        var antallTekst = antall === 1 ? null : antall;

        return (
            <div className={antallCls} aria-hidden="true">{antallTekst}</div>
        );
    }
};

export default AntallMeldinger;