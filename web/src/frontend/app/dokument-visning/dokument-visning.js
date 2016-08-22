import React, { PropTypes as PT } from 'react';
import { Dokumenter, Hurtignavigering } from 'react-dokumentvisning';
import FixedPosition from './../utils/fixed-position';
import Breadcrumbs from '../brodsmulesti/custom-breadcrumbs';

const offset = [-300, -80];

class DokumentVisning extends React.Component {
    componentDidMount() {
        document.body.scrollTop = 1;
        document.documentElement.scrollTop = 1;
    }

    render() {
        const { dokumentmetadata, journalpostmetadata, routes, params } = this.props;

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} />
                <section className="dokumenter">
                    <FixedPosition>
                        <Hurtignavigering dokumentmetadata={dokumentmetadata} navigeringsknappOffset={offset} />
                    </FixedPosition>
                    <Dokumenter
                        journalpostId={journalpostmetadata.resultat.journalpostId}
                        dokumentmetadata={dokumentmetadata}
                    />
                </section>
            </div>
        );
    }
}

DokumentVisning.propTypes = {
    dokumentmetadata: PT.array.isRequired,
    journalpostmetadata: PT.object.isRequired,
    params: PT.object.isRequired,
    routes: PT.array.isRequired
};

export default DokumentVisning;
