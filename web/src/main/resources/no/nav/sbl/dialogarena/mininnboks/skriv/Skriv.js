var React = require('react/addons');
var Link = require('react-router').Link;
var ExpandingTextArea = require('../expandingtextarea/ExpandingTextArea');
var FeedbackForm = require('../feedback/FeedbackForm');
var GodtaVilkar = require('./GodtaVilkar');
var Kvittering = require('./Kvittering');
var Feilmelding = require('../feilmelding/Feilmelding');
var InfoBoks = require('../infoboks/Infoboks');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var Utils = require('../utils/Utils');


var Skriv = React.createClass({
    getInitialState: function () {
        return {sender: false, sendt: false, sendingfeilet: false};
    },
    onSubmit: function (evt) {
        evt.preventDefault();

        var form = this.refs.form;
        form.validate();
        if (form.isValid()) {
            this.setState({sender: true});
            var temagruppe = this.props.params.temagruppe;
            var fritekst = form.getFeedbackRef('textarea').getInput();
            $.ajax({
                type: 'POST',
                url: '/mininnboks/tjenester/traader/sporsmal',
                contentType: 'application/json',
                data: JSON.stringify({temagruppe: temagruppe, fritekst: fritekst}),
                beforeSend: Utils.addXsrfHeader
            })
                .done(function () {
                    this.setState({sender: false, sendt: true});
                }.bind(this))
                .fail(function () {
                    this.setState({sendingfeilet: true, sender: false})
                }.bind(this));
        }
    },
    render: function () {
        if (['ARBD', 'FMLI', 'HJLPM', 'ORT_HJE', 'BIL', 'OK_SOS'].indexOf(this.props.params.temagruppe) < 0) {
            return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon={true} />;
        }

        if (this.state.sendt) {
            return <Kvittering resources={this.props.resources} />
        } else {

            var knapper;
            if (this.state.sender) {
                knapper = <Snurrepipp storrelse="48" farge="hvit" />;
            } else {
                knapper = (<div>
                    <div>
                        <input type="submit" className="send-link knapp-hoved-stor" role="button"
                            value={this.props.resources.get("send-sporsmal.still-sporsmal.send-inn")}
                            onClick={this.onSubmit}/>
                    </div>
                    <div className="avbryt">
                        <Link to="innboks">{this.props.resources.get("send-sporsmal.still-sporsmal.avbryt")}</Link>
                    </div>
                </div>);
            }
            var infoboks = this.state.sendingfeilet ?
                <InfoBoks.Feil>
                    <p>{this.props.resources.get('send-sporsmal.still-sporsmal.underliggende-feil')}</p>
                </InfoBoks.Feil>
                : null;

            return (
                <div>
                    <h1 className="diger">{this.props.resources.get('send-sporsmal.still-sporsmal.ny-melding-overskrift')}</h1>
                    <article className="send-sporsmal-container send-panel">
                        <div className="sporsmal-header">
                            <img src="/mininnboks/build/img/melding_graa.svg"
                                alt={this.props.resources.get("meldingikon.alternativ.tekst")}/>

                            <h2 className="stor deloverskrift">{this.props.resources.get("send-sporsmal.still-sporsmal.deloverskrift")}</h2>

                            <div className="robust-strek"></div>
                        </div>

                        <strong>{this.props.resources.get(this.props.params.temagruppe)}</strong>

                        <p className="hjelpetekst">{this.props.resources.get("send-sporsmal.still-sporsmal.hjelpetekst")}</p>
                        <FeedbackForm ref="form">
                            {infoboks}
                            <ExpandingTextArea placeholder={this.props.resources.get('skriv-sporsmal.fritekst.placeholder')}
                                charsLeftText={this.props.resources.get('traadvisning.besvar.tekstfelt.tegnigjen')}
                                feedbackref="textarea"/>

                            <GodtaVilkar feedbackref="godtavilkar" />

                            {knapper}
                        </FeedbackForm>
                    </article>
                </div>
            );
        }
    }
});

module.exports = Skriv;