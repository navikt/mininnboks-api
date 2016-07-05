import React from 'react'
import { connect } from 'react-redux';
import { lesDokumentVarsel } from '../utils/actions/actions';
import { hentDokumentVisningData } from './varsel-actions';
import { Dokumenter, Hurtignavigering } from 'react-dokumentvisning';
import { injectIntl, intlShape } from 'react-intl';
import Breadcrumbs from '../utils/brodsmulesti/customBreadcrumbs';

class DokumentVarsel extends React.Component {
    componentDidMount() {
        const { dispatch, params, traader } = this.props;
        const traad = traader.filter( (traad) => traad.traadId === params.id)[0];
        if(traad && !traad.meldinger[0].lest) {
            dispatch(lesDokumentVarsel(params.id));
        }
        if(traad && traad.meldinger[0]) {
            const varsel = traad.meldinger[0];
            dispatch(hentDokumentVisningData(varsel.journalpostId, varsel.dokumentIdListe.join('-')));
        }
    }

    render() {
        if(!this.props.dokumentvisning) {
            return <noscript/>;
        }

        const { dokumentmetadata, journalpostmetadata } = this.props.dokumentvisning;
        const { params, routes, intl } = this.props;

        return (
            <div className="dokinnsyn">
                <Breadcrumbs routes={routes} params={params} formatMessage={intl.formatMessage}/>
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

export default injectIntl(connect(mapStateToProps)(DokumentVarsel));
