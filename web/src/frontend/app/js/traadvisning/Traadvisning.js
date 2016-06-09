import React, { PropTypes as pt } from 'react';
import BesvarBoks from'./BesvarBoks';
import MeldingContainer from './MeldingContainer';
import SkrivKnapp from './SkrivKnapp';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import Feilmelding from '../feilmelding/Feilmelding';
import InfoBoks from '../infoboks/Infoboks';
import { lesTraad, resetInputState } from '../utils/actions/Actions';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/CustomBreadcrumbs';

class TraadVisning extends React.Component {

    componentWillMount() {
        const { dispatch } = this.props;
        dispatch(resetInputState());
    }

    componentDidMount() {
        const { dispatch, params } = this.props;
        dispatch(lesTraad(params.traadId));
    }

    render() {
        const { routes, params, intl: { formatMessage }, sendingStatus, traader, skrivSvar, harSubmittedSkjema, fritekst } = this.props;

        if (!traader) {
            return <Spinner spin/>;
        }

        const traadId = this.props.params.traadId;
        const valgttraad = traader.find(traad => traad.traadId === traadId);

        const meldingItems = valgttraad.meldinger.map(melding => <MeldingContainer key={melding.id} melding={melding} formatMessage={formatMessage} />);

        const overskrift = valgttraad.nyeste.kassert ?
            formatMessage({ id: 'traadvisning.overskrift.kassert' } ) :
            formatMessage({ id: 'traadvisning.overskrift' }, { temagruppeNavn: valgttraad.nyeste.temagruppeNavn });

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} formatMessage={formatMessage} />
                <h1 className="typo-sidetittel text-center blokk-l">{overskrift}</h1>
                <div className="traad-container">
                    <SkrivKnapp kanBesvares={valgttraad.kanBesvares} formatMessage={formatMessage} skrivSvar={skrivSvar} />
                    <InfoBoks formatMessage={formatMessage} sendingStatus={sendingStatus} />
                    <BesvarBoks formatMessage={formatMessage} fritekst={fritekst} skrivSvar={skrivSvar} harSubmittedSkjema={harSubmittedSkjema} traadId={traadId}/>
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

const mapStateToProps = ({ traader, harSubmittedSkjema, skrivSvar, fritekst, sendingStatus   }) => ({ traader, harSubmittedSkjema, skrivSvar, fritekst, sendingStatus  });

export default injectIntl(connect(mapStateToProps)(TraadVisning));
