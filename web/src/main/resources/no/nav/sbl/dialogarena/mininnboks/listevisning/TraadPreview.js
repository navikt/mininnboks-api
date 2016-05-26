import React from 'react';
import { Link } from 'react-router';
import AntallMeldinger from './AntallMeldinger';
import MeldingStatus from './MeldingStatus';
import Utils from '../utils/Utils';
import Constants from '../utils/Constants';
import createFragment from 'react-addons-create-fragment';

const skjermleserStatus = {};
skjermleserStatus[Constants.BESVART] = 'Besvart';
skjermleserStatus[Constants.IKKE_LEST] = 'Ikke lest';
skjermleserStatus[Constants.LEST_UBESVART] = 'Lest ubesvart';
skjermleserStatus[Constants.LEST] = 'Lest';

function skjermleserAntall(antall) {
    return antall + (antall === 1 ? ' melding' : ' meldinger');
}

class TraadPreview extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick() {
        this.props.setValgtTraad(this.props.traad);
    }

    render() {
        const { traad: { nyeste, meldinger }, resources } = this.props;

        const melding = nyeste;
        const status = Utils.status(melding);
        const antallMeldinger = meldinger.length;

        const dato = Utils.prettyDate(melding.opprettet);
        let avsnitt = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.tilAvsnitt());
        avsnitt = createFragment({ avsnitt });

        const purring = melding.type === 'SPORSMAL_MODIA_UTGAAENDE' ?
            <span className="purring">{resources.get('purre.svar')}</span> : <noscript/>;

        return (
            <Link to={`/mininnboks/traad/${melding.traadId}`} className={'traadlistevisning ' + status}
              onClick={this.onClick}>
                <AntallMeldinger antall={antallMeldinger}/>
                <MeldingStatus melding={melding}/>

                <div className="melding">
                    <p className="vekk">{skjermleserStatus[status]}</p>

                    <h2 className="status">{melding.statusTekst}</h2>

                    <p className="vekk">{skjermleserAntall(antallMeldinger)}</p>

                    <p className="dato">{dato}</p>
                    {purring}
                    <div className="fritekst">{avsnitt}</div>
                </div>
            </Link>
        );
    }
}

export default TraadPreview;
