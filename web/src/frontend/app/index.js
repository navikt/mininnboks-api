import 'babel-polyfill';
import moment from 'moment';
moment.locale('nb');

import React from 'react';
import { Router, Route, IndexRoute, applyRouterMiddleware } from 'react-router';
import createStore from './store';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { useScroll } from 'react-router-scroll';
import Listevisning from './listevisning/listevisning';
import Traadvisning from './traadvisning/traadvisning';
import Oppgavevisning from './oppgave-visning/oppgave-visning';
import SkrivNyttSporsmal from './skriv-nytt-sporsmal/skriv-nytt-sporsmal';
import Application from './application';
import DokumentVisningSide from './dokument-visning/dokument-visning-side';
import Traader from './traader/traader';
import history from './history';

const store = createStore(history);

const scrollHelper = (prevRouterProps, newRouterProps) => {
    if (!prevRouterProps || !newRouterProps) {
        return true;
    }
    if (newRouterProps.location.hash !== prevRouterProps.location.hash) {
        return false; // Ignore endringer til hash i url og la browseren håndtere denne scrollingen selv.
    }
    return true;
};

render((
    <Provider store={store}>
        <Router history={history} render={applyRouterMiddleware(useScroll(scrollHelper))}>
            <Route path="/" component={Application} breadcrumbIgnore>
                <Route breadcrumbName="Min innboks" >
                    <Route breadcrumbIgnore component={Traader}>
                        <IndexRoute component={Listevisning} breadcrumbIgnore />
                        <Route path="/traad/:traadId" component={Traadvisning} breadcrumbName=":tema" />
                        <Route path="/dokument/:id" component={DokumentVisningSide} breadcrumbName="Dokumentvisning" />
                        <Route path="/oppgave/:id" component={Oppgavevisning} breadcrumbName="Oppgavevisning" />
                    </Route>
                    <Route
                        path="/sporsmal/skriv/:temagruppe"
                        component={SkrivNyttSporsmal}
                        breadcrumbName="Ny melding"
                    />
                </Route>
            </Route>
        </Router>
    </Provider>)
, document.getElementById('mainapp'));
