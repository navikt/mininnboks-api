import React, { Component } from 'react';
import { hentTraader } from './../ducks/traader';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
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

Traader.propTypes = {};

const mapStateToProps = ({ traader }) => ({ traader });
const mapDispatchToProps = (dispatch) => ({ actions: bindActionCreators({ hentTraader }, dispatch) });

export default connect(mapStateToProps, mapDispatchToProps)(Traader);
