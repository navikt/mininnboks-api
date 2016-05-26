import React, { PropTypes as pt } from 'react';
import TraadPreview from './TraadPreview';

class TraadContainer extends React.Component {
    render() {
        const traader = this.props.traader.map(function (traad) {
                return <TraadPreview key={traad.traadId} traad={traad} setValgtTraad={this.props.setValgtTraad} resources={this.props.resources}/>;
            }.bind(this));

        return (
            <div>{traader}</div>
        );
    }
}

export default TraadContainer;
