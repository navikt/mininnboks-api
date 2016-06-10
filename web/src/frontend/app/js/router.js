import React from 'react';
import { Router, Route, IndexRoute, useRouterHistory } from 'react-router';
import ListeVisning from './listevisning/ListeVisning';
import TraadVisning from './traadvisning/Traadvisning';
import Skriv from './skriv/Skriv';
import App from './Application';
import { greedyRender } from './utils/brodsmulesti/CustomBreadcrumbs';
import { createHistory } from 'history';

const history = useRouterHistory(createHistory)({ basename: '/mininnboks/app' });

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
            <Route path="sporsmal/skriv/:temagruppe" component={greedyRender(Skriv)} breadcrumbName="Ny melding"/>
        </Route>
    </Router>
);