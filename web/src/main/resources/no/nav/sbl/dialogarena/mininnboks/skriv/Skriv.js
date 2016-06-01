import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import FeedbackForm from '../feedback/FeedbackForm';
import GodtaVilkar from './GodtaVilkar';
import Kvittering from './Kvittering';
import Feilmelding from '../feilmelding/Feilmelding';
import InfoBoks from '../infoboks/Infoboks';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import Utils from '../utils/Utils';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';

class Skriv extends React.Component {
    constructor(props) {
        super(props);
        this.state = { sender: false, sendt: false, sendingfeilet: false };
        this.onSubmit = this.onSubmit.bind(this);
    }

    componentWillMount() {
        this.godkjenteForSporsmal = this.props.intl.formatMessage({ id: 'temagruppe.liste' }).split(' ');
    }

    onSubmit(evt) {
        evt.preventDefault();

        const form = this.refs.form;
        form.validate();
        if (form.isValid()) {
            this.setState({ sender: true });
            const temagruppe = this.props.params.temagruppe;
            const fritekst = form.getFeedbackRef('textarea').getInput();
            $.ajax({
                type: 'POST',
                url: '/mininnboks/tjenester/traader/sporsmal',
                contentType: 'application/json',
                data: JSON.stringify({ temagruppe, fritekst }),
                beforeSend: Utils.addXsrfHeader
            })
                .done(function (response, status, xhr) {
                    if (xhr.status !== 201) {
                        this.setState({ sendingfeilet: true, sender: false });
                    } else {
                        this.setState({ sender: false, sendt: true });
                    }
                }.bind(this))
                .fail(function () {
                    this.setState({ sendingfeilet: true, sender: false });
                }.bind(this));
        }
    }

    render() {
        const { params, intl: { formatMessage }, visModal } = this.props;

        if (this.godkjenteForSporsmal.indexOf(params.temagruppe) < 0) {
            return <Feilmelding melding="Ikke gjenkjent temagruppe." visIkon/>;
        }

        if (this.state.sendt) {
            return <Kvittering formatMessage={formatMessage}/>;
        }

        let knapper;
        if (this.state.sender) {
            knapper = <Snurrepipp storrelse="48" farge="hvit"/>;
        } else {
            knapper = (
                <div>
                    <div>
                        <input type="submit" className="send-link knapp-hoved-stor" role="button"
                          value={formatMessage({ id: 'send-sporsmal.still-sporsmal.send-inn' })}
                          onClick={this.onSubmit}
                        />
                    </div>
                    <div className="avbryt">
                        <Link to="/mininnboks/">
                            {formatMessage({ id: 'send-sporsmal.still-sporsmal.avbryt' })}
                        </Link>
                    </div>
                </div>
            );
        }

        const infoboks = this.state.sendingfeilet ?
            <InfoBoks.Feil>
                <p>{formatMessage({ id: 'send-sporsmal.still-sporsmal.underliggende-feil' })}</p>
            </InfoBoks.Feil>
            : null;

        return (
            <div>
                <h1 className="diger">{formatMessage({ id: 'send-sporsmal.still-sporsmal.ny-melding-overskrift' })}</h1>
                <article className="send-sporsmal-container send-panel">
                    <div className="sporsmal-header">
                        <img src="/mininnboks/build/img/melding_graa.svg"
                          alt={formatMessage({ id: 'meldingikon.alternativ.tekst' })}
                        />

                        <h2 className="stor deloverskrift">{formatMessage({ id: 'send-sporsmal.still-sporsmal.deloverskrift' })}</h2>

                        <div className="robust-strek"></div>
                    </div>

                    <strong>{formatMessage({ id: params.temagruppe })}</strong>

                    <p className="hjelpetekst">{formatMessage({ id: 'send-sporsmal.still-sporsmal.hjelpetekst' })}</p>
                    <FeedbackForm ref="form">
                        {infoboks}
                        <ExpandingTextArea
                          placeholder={formatMessage({ id: 'skriv-sporsmal.fritekst.placeholder' })}
                          charsLeftText={formatMessage({ id: 'traadvisning.besvar.tekstfelt.tegnigjen' })}
                          feedbackref="textarea"
                        />

                        <GodtaVilkar feedbackref="godtavilkar" formatMessage={formatMessage} visModal={visModal}/>
                        {knapper}
                    </FeedbackForm>
                </article>
            </div>
        );
    }
}

Skriv.propTypes = {
    params: pt.shape({
        temagruppe: pt.string
    }),
    intl: intlShape.isRequired,
    visModal: pt.bool.isRequired
};

const mapStateToProps = ({ visModal }) => ({ visModal });

export default injectIntl(connect(mapStateToProps)(Skriv));
