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

export default() => (
    <Router history={history}>
        <Route path="/" component={App} breadcrumbIgnore>
            <IndexRoute component={ListeVisning} breadcrumbName="Min innboks" />
            <Route path="/" breadcrumbName="Min innboks" >
                <Route path="/traad/:traadId" component={TraadVisning} breadcrumbName=":tema" />
                <Route path="sporsmal/skriv/:temagruppe" component={Skriv} breadcrumbName="Ny melding"/>
                <Route path="dokument/:id" component={DokumentVarsel} breadcrumbName="Dokumentvisning"/>
            </Route>
            <Route path="sporsmal/skriv/:temagruppe" component={Skriv} breadcrumbName="Ny melding"/>
            <Route path="print/:journalpostid/:dokumentreferanse" component={PrintPage}/>
        </Route>
    </Router>
);
