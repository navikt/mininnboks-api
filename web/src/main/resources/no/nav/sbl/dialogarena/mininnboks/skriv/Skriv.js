var React = require('react');
var Link = require('react-router').Link;
var Resources = require('../resources/Resources');
var Utils = require('../utils/Utils');
var Snurrepipp = require('../snurrepipp/Snurrepipp');
var ExpandingTextArea = require('../expandingtextarea/ExpandingTextArea');

var Skriv = React.createClass({
    getInitialState: function () {
        return {hentet: false};
    },
    componentDidMount: function () {
        Utils.whenFinished([Resources.promise]).then(function () {
            this.setState({hentet: true})
        }.bind(this))
    },
    render: function () {
        return this.state.hentet ? (
            <div className="innboks-container">
                <h1 className="diger">{Resources.get('send-sporsmal.still-sporsmal.ny-melding-overskrift')}</h1>
                <article className="send-sporsmal-container send-panel">
                    <form>
                        <div className="sporsmal-header">
                            <img src="/mininnboks/img/melding_graa.svg"
                                 alt={Resources.get("meldingikon.alternativ.tekst")}/>

                            <h2 className="stor deloverskrift">{Resources.get("send-sporsmal.still-sporsmal.deloverskrift")}</h2>

                            <div className="robust-strek"></div>
                        </div>

                        <strong>{Resources.get(this.props.params.temagruppe)}</strong>


                        <p className="hjelpetekst">{Resources.get("send-sporsmal.still-sporsmal.hjelpetekst")}</p>
                        <ExpandingTextArea placeholder={Resources.get('skriv-sporsmal.fritekst.placeholder')}
                                           charsLeftText={Resources.get('traadvisning.besvar.tekstfelt.tegnigjen')}
                                           ref="textarea"/>

                        <div className="betingelsevalgpanel">
                            <div className="checkbox">
                                <input type="checkbox" name="betingelseValg:betingelserCheckbox" id="betingelser"/>
                                <label htmlFor="betingelser"><span>{Resources.get("send-sporsmal.still-sporsmal.betingelser.sjekkboks")}</span></label>
                                <a href="#" className="vilkarlenke">{Resources.get("send-sporsmal.still-sporsmal.betingelser.vis")}</a>
                            </div>
                        </div>

                        <div><input type="submit" className="send-link knapp-hoved-stor" role="button"
                                    value={Resources.get("send-sporsmal.still-sporsmal.send-inn")}/></div>
                        <div className="avbryt"><Link
                            to="innboks">{Resources.get("send-sporsmal.still-sporsmal.avbryt")}</Link></div>
                    </form>
                </article>
            </div>
        ) : <Snurrepipp />;
    }
});

module.exports = Skriv;