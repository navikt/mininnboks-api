var React = require('react');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;
var RouteHandler = Router.RouteHandler;
var ListeVisning = require('./listevisning/ListeVisning');
var TraadVisning = require('./traadvisning/Traadvisning');

//Include Logger for å få satt opp en global error handler
var Logger = require('./Logger');

var App = React.createClass({
    getInitialState: function () {
      return {valgtTraad: null}
    },
    setValgtTraad: function(traad) {
        this.setState({valgtTraad: traad});
    },
    render: function () {
        return (
            <RouteHandler {...this.props} valgtTraad={this.state.valgtTraad} setValgtTraad={this.setValgtTraad}/>
        );
    }
});

var routes = (
    <Route name="app" path="mininnboks/" handler={App}>
        <Route name="innboks" path="innboks" handler={ListeVisning}/>
        <Route name="traad" path="traad/:traadId" handler={TraadVisning}/>
        <DefaultRoute handler={ListeVisning}/>
    </Route>
);

Router.run(routes, Router.HistoryLocation, function (Handler, state) {
    React.render(<Handler params={state.params}/>, document.getElementById("mainapp"));
});
