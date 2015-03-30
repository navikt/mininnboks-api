var React = require('react');
var page = require('page');
var ListeVisning = require('./listevisning/ListeVisning');
var TraadVisning = require('./traadvisning/Traadvisning');


var Router = React.createClass({
    getInitialState: function() {
        return {component: <div/>};
    },
    componentDidMount: function() {
        var self = this;
        page('/mininnboks', function(ctx) {
            self.setState({component: <ListeVisning />});
        });
        page('/mininnboks/traad/:id', function (ctx) {
            self.setState({component: <TraadVisning id={ctx.params.id}/>});
        });
        page.start();
    },
    render: function() {
        return this.state.component;
    }
});

module.exports = Router;