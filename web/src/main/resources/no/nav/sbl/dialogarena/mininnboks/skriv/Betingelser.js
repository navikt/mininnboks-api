var React = require('react');
var Modal = require('../modal/Modal');

var Betingelser = React.createClass({
    submit: function () {
    },
    vis: function(){
        this.refs.modal.open();
    },
    skjul: function(){
        this.refs.modal.close();
    },
    render: function () {
        console.log('this.props', this.props);
        return (
            <Modal ref="modal">
                <form onSubmit={this.submit}>
                    <h1 className="stor">Vilkårrene for å bruke denne tjenesten</h1>
                    <div className="robust-strek"></div>
                    <p>Innhold her</p>
                    <p>Innhold her</p>
                    <p>Innhold her</p>
                    <ul>
                        <li>Punktliste</li>
                        <li>Punktliste</li>
                        <li>Punktliste</li>
                        <li>Punktliste</li>
                    </ul>
                    <p>Innhold her</p>
                    <p>Innhold her</p>
                    <p>Innhold her</p>
                    <hr />
                    <a href="javascript:void(0)" class="svar-godta knapp-hoved-stor" onClick={this.props.godta}>Jeg godtar vilkårene</a>
                    <div className="avbryt">
                        <a href="javascript:void(0)" class="svar-avbryt" onClick={this.props.avbryt}>Jeg godtar ikke vilkårene</a>
                    </div>
                </form>
            </Modal>
        );
    }
});

module.exports = Betingelser;