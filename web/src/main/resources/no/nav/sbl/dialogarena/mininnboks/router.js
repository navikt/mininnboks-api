import React from 'react';
import { Router, Route, IndexRoute, useRouterHistory, browserHistory } from 'react-router';
import ListeVisning from './listevisning/ListeVisning';
import TraadVisning from './traadvisning/Traadvisning';
import Skriv from './skriv/Skriv';
import App from './Application';

export default() => (
    <Router history={browserHistory}>
        <Route path="mininnboks/" component={App}>
            <IndexRoute component={ListeVisning} />
            <Route path="traad/:traadId" component={TraadVisning}/>
            <Route path="sporsmal/skriv/:temagruppe" component={Skriv}/>
        </Route>
    </Router>
);
