import React from 'react';

function Spinner({ spin }) {
    return <div className={spin ? 'spinner' : ''} />;
}

Spinner.propTypes = { spin: React.PropTypes.bool };

export default Spinner;
