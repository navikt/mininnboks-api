import React from 'react';

const Spinner =
    props => props.spin ? <div className="spinner"></div> : <div/>;

Spinner.propTypes = { spin: React.PropTypes.bool };

export default Spinner;
