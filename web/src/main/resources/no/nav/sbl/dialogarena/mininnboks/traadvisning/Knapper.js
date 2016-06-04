import React, { PropTypes as pt } from 'react';
import { Link } from 'react-router';
import { settSkrivSvar } from '../utils/actions/actions';
import { injectIntl, intlShape } from 'react-intl';
import { connect } from 'react-redux';

const _onClick = (dispatch) => () => dispatch(settSkrivSvar(true));

class Knapper extends React.Component {

    render() {
        const { dispatch, formatMessage, kanBesvares } = this.props;
        
        const skrivSvar = kanBesvares ?
            <button onClick={_onClick(dispatch)} className="knapp knapp-hoved knapp-liten">
                {formatMessage({ id: 'traadvisning.skriv.svar.link' })}
            </button> :
            <noscript/>;

        return (
            <div className="innboks-navigasjon">
                {skrivSvar}
            </div>
        );
    }
}

Knapper.propTypes = {
    formatMessage: pt.func.isRequired
};


export default injectIntl(connect()(Knapper));
