import React from 'react';
import ValidatableMixin from '../feedback/ValidatableMixin';
import Resources from '../resources/Resources';
import Betingelser from './Betingelser';
import { injectIntl, intlShape } from 'react-intl';

var GodtaVilkar = React.createClass({
    mixins: [ValidatableMixin],
    getInitialState() {
        return { checked: false };
    },

    onChange(event) {
        const checked = event.target.checked;
        this.setState({ checked, ariastate: 'Checkbox '+(checked ? 'avkrysset' : 'ikke avkrysset') });
        this.validate(checked);
    },

    onBlur() {
        this.validate();
    },

    validate(checked) {
        if (typeof checked === 'undefined') {
            checked = this.state.checked;
        }
        if (checked) {
            this.valid();
        } else {
            this.error(formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.feilmelding.ikke-akseptert' }));
        }
    },
    
    visbetingelser(e) {
        this.refs.betingelserPanel.vis();
        e.preventDefault();
    },

    betingelseCallback(status) {
        this.refs.betingelserPanel.skjul();
        this.onChange({ target: { checked: status } });
    },

    render() {
        const { formatMessage } = this.props.intl;
        const errorMessages = this.getErrorMessages();
        const validationErrorClass = errorMessages.length === 0 ? '' : 'validation-error error';
        const validationMessages = this.props.showInline ? this.getErrorElements('span.checkbox-feilmelding', '-inline') : null;

        return (
            <div className="betingelsevalgpanel">
                <div className="checkbox">
                    <span className="vekk" role="alert" aria-live="assertive" aria-atomic="true">{this.state.ariastate}</span>
                    <input type="checkbox" name="betingelseValg:betingelserCheckbox" className="betingelseCheckboks" id="betingelser"
                      onChange={this.onChange} onBlur={this.onBlur} checked={this.state.checked}
                      aria-invalid={!this.isValid()} aria-describedby={this.getErrorElementId('-inline')}
                    />
                    <label htmlFor="betingelser"
                        className={validationErrorClass}>
                        <span>{formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.sjekkboks' })}</span>
                        <a href="#" className="vilkarlenke" onClick={this.visbetingelser}>{formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.vis' })}</a>
                    </label>
                    <Betingelser ref="betingelserPanel" godta={this.betingelseCallback.bind(this, true)}
                                 avbryt={this.betingelseCallback.bind(this, false)} formatMessage={formatMessage}/>
                    {validationMessages}
                </div>
            </div>
        );
    }
});

GodtaVilkar.propTypes = {
    intl: intlShape
};

export default injectIntl(GodtaVilkar);
