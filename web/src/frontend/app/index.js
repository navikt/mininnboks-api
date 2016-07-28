import 'babel-polyfill';
import moment from 'moment';
moment.locale('nb');

import React from 'react';
import { Router, Route, IndexRoute, useRouterHistory } from 'react-router';
import { render } from 'react-dom';
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux';
import thunkMiddleware from 'redux-thunk';
import mainReducer from './utils/reducers/reducer';

import Listevisning from './listevisning/listevisning';
import Traadvisning from './traadvisning/traadvisning';
import SkrivNyttSporsmal from './skriv-nytt-sporsmal/skriv-nytt-sporsmal';
import Application from './application';
import DokumentVisningSide from './dokument-visning/dokument-visning-side';
import PrintPage from './print/print-page';

import { createHistory } from 'history';

const history = useRouterHistory(createHistory)({ basename: '/mininnboks' });

const store = applyMiddleware(thunkMiddleware)(createStore)(mainReducer);

render((
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={Application} breadcrumbIgnore>
                <IndexRoute component={Listevisning} breadcrumbName="Min innboks" />
                <Route breadcrumbName="Min innboks" >
                    <Route path="/traad/:traadId" component={Traadvisning} breadcrumbName=":tema" />
                    <Route
                        path="/sporsmal/skriv/:temagruppe"
                        component={SkrivNyttSporsmal}
                        breadcrumbName="Ny melding"
                    />
                    <Route path="/dokument/:id" component={DokumentVisningSide} breadcrumbName="Dokumentvisning" />
                </Route>
                <Route path="print/:journalpostid/:dokumentreferanse" component={PrintPage} />
            </Route>
        </Router>
    </Provider>)
, document.getElementById('mainapp'));
