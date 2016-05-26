import './console-polyfill';
import React from 'react';
import resources from './resources/Resources';
import ListeVisning from './listevisning/ListeVisning';
import TraadVisning from './traadvisning/Traadvisning';
import Snurrepipp from './snurrepipp/Snurrepipp';
import Feilmelding from './feilmelding/Feilmelding';
import Skriv from './skriv/Skriv';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import { render } from 'react-dom';

// Include Logger for å få satt opp en global error handler
import Logger from './Logger';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.setValgtTraad = this.setValgtTraad.bind(this);
        this.state = { valgtTraad: null, resources: resources };
    }
    
    componentDidMount() {
        const self = this;
        this.state.resources.fetch()
            .done(function () {
                self.setState({ resources: resources });
            }).fail(function () {
                self.setState({ resources: resources });
            });
    }

    setValgtTraad(traad) {
        this.setState({ valgtTraad: traad });
    }

    render() {
        const state = this.state;
        const resourcesState = this.state.resources.getPromise().state();
        let content;
        if (resourcesState === 'pending') {
            content = <Snurrepipp />;
        } else if (resourcesState === 'rejected') {
            content = <Feilmelding visIkon={true} melding="Kunne ikke hente ut standardtekster for denne applikasjonen." />;
        } else {
            content = React.cloneElement(this.props.children,
                { valgtTraad: state.valgtTraad, resources: state.resources, setValgtTraad: this.setValgtTraad }
            );
        }

        return (
            <div className="innboks-container">
                {content}
            </div>
        );
    }
}

const routes = (
    <Router history={browserHistory}>
        <Route path="mininnboks/" component={App}>
            <IndexRoute component={ListeVisning} />
            <Route path="traad/:traadId" component={TraadVisning}/>
            <Route path="sporsmal/skriv/:temagruppe" component={Skriv}/>
        </Route>
    </Router>
);

document.addEventListener('DOMContentLoaded', () => {
    render(routes, document.getElementById('mainapp'));
});
