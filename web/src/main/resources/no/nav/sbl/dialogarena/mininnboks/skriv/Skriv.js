var React = require('react');
var Link = require('react-router').Link;
var Resources = require('../resources/Resources');
var Utils = require('../utils/Utils');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var ExpandingTextArea = require('../expandingtextarea/ExpandingTextArea');
var FeedbackForm = require('../feedback/FeedbackForm');
var GodtaVilkar = require('./GodtaVilkar');

var Skriv = React.createClass({
    getInitialState: function () {
        return {hentet: false};
    },
    componentDidMount: function () {
        Utils.whenFinished([Resources.promise]).then(function () {
            this.setState({hentet: true})
        }.bind(this))
    },
    onSubmit: function() {
        var form = this.refs.form;
        form.validate();
        if (form.isValid()) {
            var temagruppe = this.props.params.temagruppe;
            var fritekst = form.getFeedbackRef('textarea').getInput();
            $.ajax({
                type: 'POST',
                url: '/mininnboks/tjenester/traader/sporsmal',
                contentType: 'application/json',
                data: JSON.stringify({temagruppe: temagruppe, fritekst: fritekst})
            }).done(function() {console.log('sendt')});
        }
    },
    render: function () {
        return this.state.hentet ? (
            <div className="innboks-container">
                <h1 className="diger">{Resources.get('send-sporsmal.still-sporsmal.ny-melding-overskrift')}</h1>
                <article className="send-sporsmal-container send-panel">
                    <div className="sporsmal-header">
                        <img src="/mininnboks/img/melding_graa.svg"
                             alt={Resources.get("meldingikon.alternativ.tekst")}/>

                        <h2 className="stor deloverskrift">{Resources.get("send-sporsmal.still-sporsmal.deloverskrift")}</h2>

                        <div className="robust-strek"></div>
                    </div>

                    <strong>{Resources.get(this.props.params.temagruppe)}</strong>

                    <p className="hjelpetekst">{Resources.get("send-sporsmal.still-sporsmal.hjelpetekst")}</p>
                    <FeedbackForm ref="form">
                        <ExpandingTextArea placeholder={Resources.get('skriv-sporsmal.fritekst.placeholder')}
                                           charsLeftText={Resources.get('traadvisning.besvar.tekstfelt.tegnigjen')}
                                           feedbackref="textarea"/>

                        <GodtaVilkar />

                        <div>
                            <input type="button" className="send-link knapp-hoved-stor" role="button"
                                   value={Resources.get("send-sporsmal.still-sporsmal.send-inn")} onClick={this.onSubmit} />
                        </div>
                        <div className="avbryt">
                            <Link to="innboks">{Resources.get("send-sporsmal.still-sporsmal.avbryt")}</Link>
                        </div>
                    </FeedbackForm>
                </article>
            </div>
        ) : <Snurrepipp />;
    }
});

module.exports = Skriv;