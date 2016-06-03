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
        const { params, intl: { formatMessage }, visModal, sporsmal_inputtekst } = this.props;

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
                        <input type="submit" className="knapp knapp-hoved knapp-stor" role="button"
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
                <h1 className="typo-sidetittel text-center blokk-l">{formatMessage({ id: 'send-sporsmal.still-sporsmal.ny-melding-overskrift' })}</h1>
                <article className="send-sporsmal-container send-panel">
                    <div className="sporsmal-header">
                        <h2 className="hode hode-innholdstittel hode-dekorert meldingikon">{formatMessage({ id: 'send-sporsmal.still-sporsmal.deloverskrift' })}</h2>
                    </div>
                    <strong>{formatMessage({ id: params.temagruppe })}</strong>

                        <ExpandingTextArea
                          charsLeftText={formatMessage({ id: 'traadvisning.besvar.tekstfelt.tegnigjen' })}
                          infotekst={formatMessage({ id: 'textarea.infotekst' })}
                          sporsmal_inputtekst={sporsmal_inputtekst}
                        />

                        <GodtaVilkar feedbackref="godtavilkar" formatMessage={formatMessage} visModal={visModal}/>
                        {knapper}

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
