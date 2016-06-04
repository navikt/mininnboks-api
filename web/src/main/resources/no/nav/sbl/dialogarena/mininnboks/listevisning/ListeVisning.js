import React, { PropTypes as pt } from 'react';
import TraaderContainer from './TraaderContainer';
import Feilmelding from '../feilmelding/Feilmelding';
import { Link } from 'react-router';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Spinner from '../Spinner';

class ListeVisning extends React.Component {

    render() {
        const { intl: { formatMessage }, traader } = this.props;

        if (!traader) {
            return <Spinner spin/>;
        }

        let content;
        if (traader.length === 0) {
            content = <Feilmelding melding={formatMessage({ id: 'innboks.tom-innboks-melding'} )}/>;
        } else {
            content = <TraaderContainer traader={traader} formatMessage={formatMessage}/>;
        }

        return (
            <div>
                <h1 className="typo-sidetittel text-center blokk-l">{formatMessage({ id: 'innboks.overskrift' })}</h1>
                <div className="innboks-navigasjon clearfix">
                     <Link to={formatMessage({ id: 'skriv.ny.link'} )} className="knapp knapp-hoved knapp-liten" >{formatMessage({ id: 'innboks.skriv.ny.link'} )}</Link>
                </div>
                {content}
            </div>
        );
    }
}

ListeVisning.propTypes = {
    intl: intlShape,
    traader: pt.array.isRequired
};

const mapStateToProps = ({ traader  }) => ({ traader });

export default injectIntl(connect(mapStateToProps)(ListeVisning));
