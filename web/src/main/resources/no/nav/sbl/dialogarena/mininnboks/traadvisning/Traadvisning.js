import React, { PropTypes as pt } from 'react';
import BesvarBoks from'./BesvarBoks';
import MeldingContainer from './MeldingContainer';
import Knapper from './Knapper';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import Feilmelding from '../feilmelding/Feilmelding';
import InfoBoks from '../infoboks/Infoboks';
import { lesTraad } from '../utils/actions/actions';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from './../utils/brodsmulesti/customBreadcrumbs';

class TraadVisning extends React.Component {

    componentDidMount() {
        const { dispatch, params } = this.props;
        dispatch(lesTraad(params.traadId));
    }

    render() {
        const { routes, params, intl: { formatMessage }, sendingStatus, traader, skrivSvar, harSubmittedSkjema, sporsmalInputtekst } = this.props;

        if (!traader) {
            return <Spinner spin/>;
        }

        const traadId = this.props.params.traadId;
        const valgttraad = traader.find(function(traad) { return traad.traadId === traadId });

        const meldingItems = valgttraad.meldinger.map(function (melding) {
            return <MeldingContainer key={melding.id} melding={melding} formatMessage={formatMessage} />;
        }.bind(this));

        const overskrift = valgttraad.nyeste.kassert ?
            formatMessage({ id: 'traadvisning.overskrift.kassert' } ) :
            formatMessage({ id: 'traadvisning.overskrift' }, { temagruppeNavn: valgttraad.nyeste.temagruppeNavn });

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} formatMessage={formatMessage} />
                <h1 className="typo-sidetittel text-center blokk-l">{overskrift}</h1>
                <div className="traad-container">
                    <Knapper kanBesvares={valgttraad.kanBesvares} formatMessage={formatMessage} />
                    <InfoBoks formatMessage={formatMessage} sendingStatus={sendingStatus} />
                    <BesvarBoks formatMessage={formatMessage} sporsmalInputtekst={sporsmalInputtekst} skrivSvar={skrivSvar} harSubmittedSkjema={harSubmittedSkjema}/>
                    {meldingItems}
                </div>
            </div>
        );
    }
}

TraadVisning.propTypes = {
    intl: intlShape,
    traader: pt.array.isRequired
};

const mapStateToProps = ({ traader, harSubmittedSkjema, skrivSvar, sporsmalInputtekst, sendingStatus   }) => ({ traader, harSubmittedSkjema, skrivSvar, sporsmalInputtekst, sendingStatus  });

export default injectIntl(connect(mapStateToProps)(TraadVisning));
