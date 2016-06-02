import React, { PropTypes as pt } from 'react';
import BesvarBoks from'./BesvarBoks';
import MeldingContainer from './MeldingContainer';
import Knapper from './Knapper';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import Feilmelding from '../feilmelding/Feilmelding';
import InfoBoks from '../infoboks/Infoboks';
import Utils from '../utils/Utils';
import { injectIntl, intlShape, FormattedMessage } from 'react-intl';
import Breadcrumbs from './../utils/brodsmulesti/customBreadcrumbs';

function okCallback(data) {
    this.setState({
        traad: data,
        hentet: true
    });
    $.ajax({
        type: 'POST',
        url: `/mininnboks/tjenester/traader/lest/${data.traadId}`,
        beforeSend: Utils.addXsrfHeader
    });
}
function feiletCallback(formatMessage) {
    this.setState({
        feilet: { status: true, melding: formatMessage({ id:'traadvisning.feilmelding.hentet-ikke-traad' }) },
        hentet: true
    });
}

function kunneIkkeLeggeTilMelding() {
    this.setState({
        sendingfeilet: true
    });
}

function leggTilMelding(fritekst, response, status, xhr, formatMessage) {
    if (xhr.status !== 201) {
        kunneIkkeLeggeTilMelding.call(this, response, status, xhr);
        return;
    }
    const meldinger = this.state.traad.meldinger.splice(0);
    meldinger.unshift({
        fritekst: fritekst,
        opprettet: new Date(),
        temagruppeNavn: this.state.traad.nyeste.temagruppeNavn,
        fraBruker: true,
        fraNav: false,
        statusTekst: formatMessage({ id: 'status.SVAR_SBL_INNGAAENDE' }).replace('%s', meldinger[0].temagruppeNavn)
    });

    this.setState({
        traad: { meldinger: meldinger, kanBesvares: false, nyeste: meldinger[0] },
        besvart: true,
        besvares: false
    });
}

class TraadVisning extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            hentet: false,
            feilet: false,
            besvares: false,
            besvart: false,
            sendingfeilet: false,
            traad: {}
        };
        this.visBesvarBoks = this.visBesvarBoks.bind(this);
        this.skjulBesvarBoks = this.skjulBesvarBoks.bind(this);
        this.sendMelding = this.sendMelding.bind(this);
        this.getInfoMelding = this.getInfoMelding.bind(this);
    }

    componentDidMount() {
        if (this.props.valgtTraad) {
            okCallback.call(this, this.props.valgtTraad);
        } else if (typeof this.props.params.traadId === 'string' && this.props.params.traadId.length > 0) {
            $.get(`/mininnboks/tjenester/traader/${this.props.params.traadId}`)
                .then(okCallback.bind(this), feiletCallback.bind(this));
        } else {
            feiletCallback.call(this, this.props.valgtTraad, this.props.formatMessage);
        }
    }

    sendMelding(fritekst) {
        return $.ajax({
            type: 'POST',
            url: '/mininnboks/tjenester/traader/svar',
            contentType: 'application/json',
            data: JSON.stringify({ traadId: this.state.traad.nyeste.traadId, fritekst: fritekst }),
            beforeSend: Utils.addXsrfHeader
        })
            .done(leggTilMelding.bind(this, fritekst))
            .fail(kunneIkkeLeggeTilMelding.bind(this));
    }

    getInfoMelding(formatMessage) {
        if (this.state.traad.avsluttet) {
            return (
                <InfoBoks.Info>
                    <p>
                        {formatMessage({ id:'traadvisning.kan-ikke-svare.info' })}
                        {' '}
                        <a href={formatMessage({ id:'skriv.ny.link' })} className="lopendetekst">
                            {formatMessage({ id: 'traadvisning.kan-ikke-svare.lenke' })}
                        </a>
                    </p>
                </InfoBoks.Info>
            );
        } else if (this.state.besvart) {
            return (
                <InfoBoks.Ok focusOnRender={true}>
                    <p dangerouslySetInnerHTML={{ __html: formatMessage({ id: 'send-svar.bekreftelse.varslingsinfo' })} }></p>
                </InfoBoks.Ok>
            );
        } else if (this.state.sendingfeilet) {
            return (
                <InfoBoks.Feil>
                    <p>{formatMessage({ id: 'besvare.feilmelding.innsending' })}</p>
                </InfoBoks.Feil>
            );
        }
        return null;
    }

    visBesvarBoks() {
        this.setState({ besvares: true });
    }

    skjulBesvarBoks() {
        this.setState({ besvares: false });
    }

    render() {
        const { intl: { formatMessage }, routes, params} = this.props;
        if (!this.state.hentet) {
            return <Snurrepipp />;
        }
        if (this.state.feilet.status) {
            return <Feilmelding melding={this.state.feilet.melding} visIkon={true} />;
        }

        const meldingItems = this.state.traad.meldinger.map(function (melding) {
            return <MeldingContainer key={melding.id} melding={melding} formatMessage={formatMessage} />;
        }.bind(this));

        const temagruppeNavn = this.state.traad.nyeste.temagruppeNavn;
        const overskrift = this.state.traad.nyeste.kassert ?
            formatMessage({ id: 'traadvisning.overskrift.kassert' } ) :
            <FormattedMessage id="traadvisning.overskrift" values={{ temagruppeNavn }}/>;

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} formatMessage={formatMessage} />
                <h1 className="typo-sidetittel text-center blokk-l">{overskrift}</h1>
                <div className="traad-container">
                    <Knapper kanBesvares={this.state.traad.kanBesvares} besvares={this.state.besvares}
                      besvar={this.visBesvarBoks} formatMessage={formatMessage}
                    />
                    {this.getInfoMelding(formatMessage)}
                    <BesvarBoks besvar={this.sendMelding} vis={this.state.besvares}
                      skjul={this.skjulBesvarBoks} formatMessage={formatMessage}
                    />
                    {meldingItems}
                </div>
            </div>
        );
    }
}

TraadVisning.propTypes = {
    params: pt.shape({
        traadId: pt.string
    }),
    valgtTraad: pt.object,
    intl: intlShape
};

export default injectIntl(TraadVisning);
