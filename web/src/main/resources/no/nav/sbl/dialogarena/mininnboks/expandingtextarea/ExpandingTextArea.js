import React, { PropTypes as pt } from 'react';
import { connect } from 'react-redux';
import { skrivTekst } from '../utils/actions/actions.js';
import { injectIntl } from 'react-intl';


class ExpandingTextArea extends React.Component {

    render() {
        const { infotekst, dispatch, sporsmal_inputtekst } = this.props;
        const resterendeLengde = 1000 - sporsmal_inputtekst.length;

        return (
            <div className="textarea-meta-container js-container">
                <label for="textarea-med-meta">
                    <span className="typo-normal max-length">{infotekst}</span>
                </label>
                <textarea id="textarea-med-meta" name="textarea-med-meta" className={`input-fullbredde typo-normal`}
                  title={this.props.placeholder}
                  aria-label={this.props.placeholder} aria-invalid="false" aria-describedby=''
                  onChange={ _onWrite(dispatch, '')}
                  value={sporsmal_inputtekst}
                />
                <p className="textarea-metatekst" aria-hidden="true">
                 <span class="max-length">{resterendeLengde}</span> tegn igjen
                </p>
            </div>
        );
    }
}

const _onWrite = (dispatch, tekst) =>
    (event) => dispatch(skrivTekst(event.target.value));

ExpandingTextArea.propTypes = {
    dispatch: pt.func.isRequired,
    sporsmal_inputtekst: pt.string.isRequired
};

export default injectIntl(connect(({ sporsmal_inputtekst }) => ({ sporsmal_inputtekst }))(ExpandingTextArea));

