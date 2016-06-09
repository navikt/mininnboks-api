import React, { PropTypes as pt } from 'react';
import { status } from '../utils/Utils';
import Constants from '../utils/Constants';

class MeldingStatus extends React.Component {
    render() {
        const status = status(this.props.melding);
        if (status === Constants.LEST) {
            return <noscript/>;
        }
        return (
            <div aria-hidden="true" className={`ikon melding-info ${status}`}></div>
        );
    }
}

MeldingStatus.propTypes = {
    melding: pt.object
};

export default MeldingStatus;
