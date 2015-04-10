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
        var content;
        if (resourcesState === 'pending') {
            content = <Snurrepipp />
        } else if (resourcesState === 'rejected') {
            content = <Feilmelding visIkon={true} melding="Kunne ikke hente ut standardtekster for denne applikasjonen." />;
        } else {
            content = <RouteHandler {...this.props} {...this.state} setValgtTraad={this.setValgtTraad}/>;
        }

        return (
            <div className="innboks-container">
                {content}
            </div>
        );
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
