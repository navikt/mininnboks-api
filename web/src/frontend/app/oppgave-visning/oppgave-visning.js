import React, { PropTypes as PT, Component } from 'react';
import { connect } from 'react-redux';
import Spinner from 'nav-react-design/dist/spinner';
import { storeShape, traadShape } from './../proptype-shapes';
import { markerTraadSomLest } from './../ducks/traader';

class OppgaveVisning extends Component {
    componentDidMount() {
        const { params, traader, actions } = this.props;
        const traad = traader.data.find((trad) => trad.traadId === params.id);

        actions.markerSomLest(params.id)
            .then(() => {
                window.location.replace(traad.nyeste.oppgaveUrl);
            });
    }

    render() {
        return <Spinner storrelse="xxl" />;
    }
}

OppgaveVisning.propTypes = {
    params: PT.object.isRequired,
    traader: storeShape(traadShape).isRequired,
    actions: PT.shape({
        markerSomLest: PT.func.isRequired
    }).isRequired
};

const mapStateToProps = ({ traader }) => ({ traader });
const mapDispatchToProps = (dispatch) => ({
    actions: {
        markerSomLest: (id) => dispatch(markerTraadSomLest(id))
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(OppgaveVisning);
