import React from 'react/addons';
import { Link } from 'react-router';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import FeedbackForm from '../feedback/FeedbackForm';
import GodtaVilkar from './GodtaVilkar';
import Kvittering from './Kvittering';
import Feilmelding from '../feilmelding/Feilmelding';
import InfoBoks from '../infoboks/Infoboks';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import Utils from '../utils/Utils';

class Skriv extends React.Component {
    constructor(props) {
        super(props);
        this.state = {sender: false, sendt: false, sendingfeilet: false};
        this.onSubmit = this.onSubmit.bind(this);
    }

    componentWillMount () {
        this.godkjenteForSporsmal = this.props.resources.get('temagruppe.liste').split(' ');
    }

    onSubmit (evt) {
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
                .done(function (response, status, xhr) {
                    if (xhr.status !== 201) {
                        this.setState({sendingfeilet: true, sender: false})
                    } else {
                        this.setState({sender: false, sendt: true});
                    }
                }.bind(this))
                .fail(function () {
                    this.setState({sendingfeilet: true, sender: false})
                }.bind(this));
        }
    }

    render () {
        if (this.godkjenteForSporsmal.indexOf(this.props.params.temagruppe) < 0) {
            return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon={true}/>;
        }

        if (this.state.sendt) {
            return <Kvittering resources={this.props.resources}/>
        } else {

            var knapper;
            if (this.state.sender) {
                knapper = <Snurrepipp storrelse="48" farge="hvit"/>;
            } else {
                knapper = (<div>
                    <div>
                        <input type="submit" className="send-link knapp-hoved-stor" role="button"
                               value={this.props.resources.get("send-sporsmal.still-sporsmal.send-inn")}
                               onClick={this.onSubmit}/>
                    </div>
                    <div className="avbryt">
                        <Link to="/mininnboks/">{this.props.resources.get("send-sporsmal.still-sporsmal.avbryt")}</Link>
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
                            <ExpandingTextArea
                                placeholder={this.props.resources.get('skriv-sporsmal.fritekst.placeholder')}
                                charsLeftText={this.props.resources.get('traadvisning.besvar.tekstfelt.tegnigjen')}
                                feedbackref="textarea"/>

                            <GodtaVilkar feedbackref="godtavilkar"/>

                            {knapper}
                        </FeedbackForm>
                    </article>
                </div>
            );
        }
    }
};

export default Skriv;