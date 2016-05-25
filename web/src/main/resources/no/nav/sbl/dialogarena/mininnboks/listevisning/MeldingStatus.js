import React from 'react/addons';
import Utils from '../utils/Utils';
import Constants from '../utils/Constants';

class MeldingStatus extends React.Component {
    render () {
        var status = Utils.status(this.props.melding);
        if (status === Constants.LEST) {
            return null;
        }
        return (
            <div aria-hidden="true" className={'ikon melding-info ' + status}></div>
        );
    }
};

export default MeldingStatus;