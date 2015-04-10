var React = require('react/addons');
var Modal = require('../modal/Modal');
var Resources = require('../resources/Resources');
var Snurrepipp = require('../snurrepipp/Snurrepipp');

var Betingelser = React.createClass({
    submit: function (e) {
        e.preventDefault();
    },
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    getInitialState: function () {
        return {
            hentet: false
        };
    },
    componentDidMount: function () {
        $.when(Resources.promise).then(function () {
            this.setState({hentet: true})
        }.bind(this))
    },
    render: function () {
        if (!this.state.hentet) {
            return <Snurrepipp />;
        }
        return (
            <Modal ref="modal">
                <form onSubmit={this.submit} className="betingelserPanel">
                    <h1 className="stor" tabIndex="0">{Resources.get("send-sporsmal.still-sporsmal.betingelser.overskrift")}</h1>
                    <div className="robust-strek"></div>
                    <div dangerouslySetInnerHTML={{__html: Resources.get("send-sporsmal.still-sporsmal.betingelser.tekst")}}></div>
                    <hr />
                    <a href="javascript:void(0)" className="svar-godta knapp-hoved-stor" onClick={this.props.godta}>{Resources.get("send-sporsmal.still-sporsmal.betingelser.godta")}</a>
                    <div className="avbryt">
                        <a href="javascript:void(0)" className="svar-avbryt" onClick={this.props.avbryt}>{Resources.get("send-sporsmal.still-sporsmal.betingelser.ikke-godta")}</a>
                    </div>
                </form>
            </Modal>
        );
    }
});

module.exports = Betingelser;