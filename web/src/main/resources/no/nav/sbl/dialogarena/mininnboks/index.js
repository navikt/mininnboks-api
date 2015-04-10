var React = require('react/addons');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;
var RouteHandler = Router.RouteHandler;
var resources = require('./resources/Resources');
var ListeVisning = require('./listevisning/ListeVisning');
var TraadVisning = require('./traadvisning/Traadvisning');
var Snurrepipp = require('./snurrepipp/Snurrepipp');
var Feilmelding = require('./feilmelding/Feilmelding');
var Skriv = require('./skriv/Skriv');

//Include Logger for å få satt opp en global error handler
var Logger = require('./Logger');

var App = React.createClass({
    getInitialState: function () {
        return {valgtTraad: null, resources: resources}
    },
    componentDidMount: function () {
        var self = this;
        this.state.resources.fetch()
            .done(function () {
                self.setState({resources: resources});
            }).fail(function () {
                self.setState({resources: resources});
            });
    },
    setValgtTraad: function (traad) {
        this.setState({valgtTraad: traad});
    },
    render: function () {
        var resourcesState = this.state.resources.getPromise().state();
        if (resourcesState === 'pending') {
            return <Snurrepipp />
        } else if (resourcesState === 'rejected') {
            return <Feilmelding visIkon={true} melding="Kunne ikke hente ut standardtekster for denne applikasjonen." />;
        } else {
            return (
                <RouteHandler {...this.props} {...this.state} setValgtTraad={this.setValgtTraad}/>
            );
        }
    }
});

var routes = (
    <Route name="app" path="mininnboks/" handler={App}>
        <Route name="innboks" path="innboks" handler={ListeVisning}/>
        <Route name="traad" path="traad/:traadId" handler={TraadVisning}/>
        <Route name="skriv" path="sporsmal/skriv/:temagruppe" handler={Skriv}/>
        <DefaultRoute handler={ListeVisning}/>
    </Route>
);

Router.run(routes, Router.HistoryLocation, function (Handler, state) {
    React.render(<Handler params={state.params}/>, document.getElementById("mainapp"));
});
