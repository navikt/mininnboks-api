import React from 'react'
import { connect } from 'react-redux';
import { lesDokumentVarsel } from '../utils/actions/Actions';


class DokumentVarsel extends React.Component {
    componentDidMount() {
        const { dispatch, params } = this.props;
        dispatch(lesDokumentVarsel(params.id));
    }

    render() {
        return (
            <div></div>
        );
    }
}

export default connect()(DokumentVarsel);
