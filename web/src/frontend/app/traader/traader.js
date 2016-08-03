import React, { PropTypes as PT, Component } from 'react';
import { hentTraader } from './../ducks/traader';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { storeShape, traadShape } from './../proptype-shapes';
import Innholdslaster from './../innholdslaster/innholdslaster';

class Traader extends Component {
    componentWillMount() {
        this.props.actions.hentTraader();
    }
    render() {
        return (
            <Innholdslaster avhengigheter={[this.props.traader]}>
                {this.props.children}
            </Innholdslaster>
        );
    }
}

Traader.propTypes = {
    traader: storeShape(traadShape).isRequired,
    actions: PT.shape({
        hentTraader: PT.func
    }).isRequired,
    children: PT.node.isRequired
};

const mapStateToProps = ({ traader }) => ({ traader });
const mapDispatchToProps = (dispatch) => ({ actions: bindActionCreators({ hentTraader }, dispatch) });

export default connect(mapStateToProps, mapDispatchToProps)(Traader);
