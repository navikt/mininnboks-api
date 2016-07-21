import React, { PropTypes as PT } from 'react';
import { connect } from 'react-redux';
import { lesDokumentVarsel } from '../utils/actions/actions';
import { hentDokumentVisningData } from './dokument-actions';
import Dokumentvisning from './dokument-visning';
import Breadcrumbs from '../utils/brodsmulesti/custom-breadcrumbs';

class DokumentVisningSide extends React.Component {
    componentDidMount() {
        const { params, traader, actions } = this.props;
        const traad = traader.find((trad) => trad.traadId === params.id);
        if (traad && !traad.meldinger[0].lest) {
            actions.lesDokumentVarsel(params.id);
        }
        if (traad && traad.meldinger[0]) {
            const varsel = traad.meldinger[0];
            actions.hentDokumentVisningData(varsel.journalpostId, varsel.dokumentIdListe.join('-'));
        }
    }

    render() {
        if (!this.props.dokumentvisning) {
            return null;
        }
        const { params, routes } = this.props;

        return (
            <div className="dokinnsyn">
                <Breadcrumbs routes={routes} params={params} />
                <Dokumentvisning {...this.props.dokumentvisning} />
            </div>
        );
    }
}

DokumentVisningSide.propTypes = {
    dokumentvisning: PT.object.isRequired,
    params: PT.object.isRequired,
    routes: PT.array.isRequired,
    traader: PT.array.isRequired,
    actions: PT.shape({
        lesDokumentVarsel: PT.func.isRequired,
        hentDokumentVisningData: PT.func.isRequired
    })
};

const mapStateToProps = ({ traader, dokumentvisning }) => ({ traader, dokumentvisning });
const mapDispatchToProps = (dispatch) => ({
    actions: {
        lesDokumentVarsel: (id) => dispatch(lesDokumentVarsel(id)),
        hentDokumentVisningData: (journalpostId, idListe) => dispatch(hentDokumentVisningData(journalpostId, idListe))
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(DokumentVisningSide);
