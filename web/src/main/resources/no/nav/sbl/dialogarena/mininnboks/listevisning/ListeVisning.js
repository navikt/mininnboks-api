import React from 'react';
import TraaderContainer from './TraaderContainer';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import Feilmelding from '../feilmelding/Feilmelding';
import { Link } from 'react-router';

function okCallback(data) {
    this.setState({
        traader: data,
        hentet: true
    });
}
function feiletCallback() {
    this.setState({
        feilet: { status: true, melding: this.props.resources.get('innboks.kunne-ikke-hente-meldinger') },
        hentet: true
    });
}

class ListeVisning extends React.Component {
    constructor(props) {
        super(props);
        this.state = { traader: [], hentet: false, feilet: { status: false } };
    }

    componentDidMount() {
        $.get('/mininnboks/tjenester/traader/').then(okCallback.bind(this), feiletCallback.bind(this));
    }

    render() {
        if (!this.state.hentet) {
            return <Snurrepipp />;
        }

        const { resources, setValgtTraad } = this.props;

        let content;
        if (this.state.feilet.status) {
            content = <Feilmelding melding={this.state.feilet.melding} />;
        } else if (this.state.traader.length === 0) {
            content = <Feilmelding melding={resources.get('innboks.tom-innboks-melding')}/>;
        } else {
            content = <TraaderContainer traader={this.state.traader} setValgtTraad={setValgtTraad} resources={resources} />;
        }

        return (
            <div>
                <h1 className="diger">{resources.get('innboks.overskrift')}</h1>
                <div className="innboks-navigasjon clearfix">
                     <Link to={resources.get('skriv.ny.link')} className="skriv-ny-link knapp-hoved-liten" >{resources.get('innboks.skriv.ny.link')}</Link>
                </div>
                {content}
            </div>
        );
    }
}

export default ListeVisning;
