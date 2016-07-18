import React from 'react';
import { Router, Route, IndexRoute, useRouterHistory } from 'react-router';
import ListeVisning from './listevisning/ListeVisning';
import TraadVisning from './traadvisning/Traadvisning';
import Skriv from './skriv/Skriv';
import App from './Application';
import { createHistory } from 'history';
import DokumentVarsel from './dokumentvarsel/DokumentVarsel';
import PrintPage from './print/print-page';

const history = useRouterHistory(createHistory)({ basename: '/mininnboks' });

function greedyRender(Component) {
    return ({ children, ...props }) => {
        if (children) {
            return children;
        }
        return <Component {...props} />
    }
}

export default() => (
    <Router history={history}>
        <Route path="/" component={App} breadcrumbIgnore>
            <IndexRoute component={ListeVisning} breadcrumbName="Min innboks" />
            <Route path="/" component={greedyRender(ListeVisning)} breadcrumbName="Min innboks" >
                <Route path="/traad/:tema/:traadId" component={greedyRender(TraadVisning)} breadcrumbName="Dialog om :tema" />
                </Route>
            <Route path="/" component={greedyRender(ListeVisning)} breadcrumbName="Min innboks" >
                <Route path="sporsmal/skriv/:temagruppe" component={greedyRender(Skriv)} breadcrumbName="Ny melding"/>
            </Route>
            <Route path="/" component={greedyRender(ListeVisning)} breadcrumbName="Min innboks" >
                <Route path="dokument/:id" component={greedyRender(DokumentVarsel)} breadcrumbName="Dokumentvisning"/>
            </Route>
            <Route path="sporsmal/skriv/:temagruppe" component={greedyRender(Skriv)} breadcrumbName="Ny melding"/>
            <Route path="print/:journalpostid/:dokumentreferanse" component={PrintPage}/>
        </Route>
    </Router>
);
