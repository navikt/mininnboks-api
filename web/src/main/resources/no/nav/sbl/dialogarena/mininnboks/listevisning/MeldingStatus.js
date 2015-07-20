var React = require('react/addons');
var Utils = require('../utils/Utils');
var Constants = require('../utils/Constants');

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