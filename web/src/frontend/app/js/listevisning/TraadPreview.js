import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import DokumentPreview from './DokumentPreview';
import MeldingPreview from './MeldingPreview';

class TraadPreview extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick() {
        this.props.setValgtTraad(this.props.traad);
    }

    render() {
        const { traad } = this.props;

        const type = traad.nyeste.type;

        if (type === 'DOKUMENT_VARSEL') {
            return <DokumentPreview {...this.props} onClick={this.onClick}/>;
        } else {
            return <MeldingPreview {...this.props} onClick={this.onClick}/>;
        }
    }
}

TraadPreview.propTypes = {
    traad: pt.object,
    formatMessage: pt.func.isRequired
};

export default TraadPreview;
