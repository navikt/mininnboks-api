import React, { PropTypes as pt } from 'react';
import TraaderContainer from './TraaderContainer';
import Feilmelding from '../feilmelding/Feilmelding';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';
import Breadcrumbs from '../utils/brodsmulesti/customBreadcrumbs';

class ListeVisning extends React.Component {

    render() {
        const { intl: { formatMessage }, routes, params, traader, location } = this.props;

        let content;
        if (traader.length === 0) {
            content = <Feilmelding melding={formatMessage({ id: 'innboks.tom-innboks-melding' })}/>;
        } else {
            content = <TraaderContainer traader={traader} formatMessage={formatMessage} location={location}/>;
        }

        return (
            <div>
                <Breadcrumbs routes={routes} params={params} formatMessage={formatMessage}/>
                <h1 className="typo-sidetittel text-center blokk-l">{formatMessage({ id: 'innboks.overskrift' })}</h1>
                <div className="innboks-navigasjon clearfix">
                    <a href={formatMessage({ id: 'skriv.ny.link' })} className="knapp knapp-hoved knapp-liten">
                        {formatMessage({ id: 'innboks.skriv.ny.link' })}
                    </a>
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

const mapStateToProps = ({ traader }) => ({ traader });

export default injectIntl(connect(mapStateToProps)(ListeVisning));
