import React from 'react/addons';
import Utils from '../utils/Utils';
import Constants from '../utils/Constants';

var MeldingStatus = React.createClass({
    render: function () {
        var status = Utils.status(this.props.melding);
        if (status === Constants.LEST) {
            return null;
        }
        return (
            <div aria-hidden="true" className={'ikon melding-info ' + status}></div>
        );
    }
});

module.exports = MeldingStatus;