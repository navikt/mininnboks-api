import React from 'react'
import { connect } from 'react-redux';
import { lesDokumentVarsel } from '../utils/actions/actions';
import { hentDokumentVisningData } from './varsel-actions';
import { Dokumenter, Hurtignavigering } from 'react-dokumentvisning';

class DokumentVarsel extends React.Component {
    componentDidMount() {
        const { dispatch, params, traader } = this.props;
        const traad = traader.filter( (traad) => traad.traadId === params.id)[0];
        if(traad && !traad.meldinger[0].lest) {
            dispatch(lesDokumentVarsel(params.id));
        }
        if(traad && traad.meldinger[0]) {
            dispatch(hentDokumentVisningData(368274526, '398128630-358128632', 'DAG'));
        }
    }

    render() {
        if(!this.props.dokumentvisning) {
            return <noscript/>;
        }

        const { dokumentmetadata, journalpostmetadata } = this.props.dokumentvisning;

        return (
            <div className="dokinnsyn">
                <section className="dokumenter">
                    <Hurtignavigering dokumentmetadata={dokumentmetadata}/>
                    <Dokumenter
                      journalpostId={journalpostmetadata.journalpostId}
                      dokumentmetadata={dokumentmetadata}
                    />
                </section>
            </div>
        );
    }
}

const mapStateToProps = ({ traader, dokumentvisning  }) => ({ traader, dokumentvisning });

export default connect(mapStateToProps)(DokumentVarsel);
