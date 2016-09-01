import 'babel-polyfill';
import moment from 'moment';
moment.locale('nb');

import React from 'react';
import { Router, Route, IndexRoute, useRouterHistory, applyRouterMiddleware } from 'react-router';
import createStore from './store';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { useScroll } from 'react-router-scroll';
import Listevisning from './listevisning/listevisning';
import Traadvisning from './traadvisning/traadvisning';
import SkrivNyttSporsmal from './skriv-nytt-sporsmal/skriv-nytt-sporsmal';
import Application from './application';
import DokumentVisningSide from './dokument-visning/dokument-visning-side';
import PrintPage from './print/print-page';
import Traader from './traader/traader';

import { createHistory } from 'history';

const history = useRouterHistory(createHistory)({ basename: '/mininnboks' });

const store = createStore(history);

render((
    <Provider store={store}>
        <Router history={history} render={applyRouterMiddleware(useScroll())}>
            <Route path="/" component={Application} breadcrumbIgnore>
                <Route breadcrumbName="Min innboks" >
                    <Route breadcrumbIgnore component={Traader}>
                        <IndexRoute component={Listevisning} breadcrumbIgnore />
                        <Route path="/traad/:traadId" component={Traadvisning} breadcrumbName=":tema" />
                        <Route path="/dokument/:id" component={DokumentVisningSide} breadcrumbName="Dokumentvisning" />
                    </Route>
                    <Route
                        path="/sporsmal/skriv/:temagruppe"
                        component={SkrivNyttSporsmal}
                        breadcrumbName="Ny melding"
                    />
                </Route>
                <Route path="print/:journalpostid/:dokumentreferanse" component={PrintPage} />
            </Route>
        </Router>
    </Provider>)
, document.getElementById('mainapp'));
