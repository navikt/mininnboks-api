import React, { PropTypes as pt } from 'react';
import { connect } from 'react-redux';
import { skrivTekst } from '../utils/actions/actions.js';
import { injectIntl } from 'react-intl';
import SamletFeilmeldingPanel from './SamletFeilmeldingPanel';
import FeilmeldingEnum from '../skriv/FeilmeldingEnum';

class ExpandingTextArea extends React.Component {

    render() {
        const { formatMessage, dispatch, sporsmalInputtekst, validationResult } = this.props;
        const resterendeLengde = 1000 - sporsmalInputtekst.length;
        const additionalClassName = validationResult.includes(FeilmeldingEnum.textarea) ? 'invalid' : '';

        return (
            <div className="textarea-meta-container js-container">
                <label for="textarea-med-meta">
                    <span className="typo-normal max-length">{formatMessage({ id: 'textarea.infotekst' })}</span>
                </label>
                <SamletFeilmeldingPanel formatMessage={formatMessage} validationResult={validationResult}/>

                <textarea id="textarea-med-meta" name="textarea-med-meta" className={`input-fullbredde typo-normal ${additionalClassName}`}
                  title={this.props.placeholder}
                  aria-label={this.props.placeholder} aria-invalid="false" aria-describedby=''
                  onChange={ _onWrite(dispatch)}
                  value={sporsmalInputtekst}
                />
                <p className="textarea-metatekst" aria-hidden="true">
                 <span class="max-length">{resterendeLengde}</span> tegn igjen
                </p>
            </div>
        );
    }
}

const _onWrite = (dispatch) => (event) => dispatch(skrivTekst(event.target.value));

ExpandingTextArea.propTypes = {
    dispatch: pt.func,
    sporsmalInputtekst: pt.string.isRequired,
    validationResult: pt.array.isRequired
};

export default connect()(ExpandingTextArea);

