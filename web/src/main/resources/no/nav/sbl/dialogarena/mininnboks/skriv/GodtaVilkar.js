import React from 'react';
import ValidatableMixin from '../feedback/ValidatableMixin';
import Resources from '../resources/Resources';
import Betingelser from './Betingelser';

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
            this.error(Resources.get('send-sporsmal.still-sporsmal.betingelser.feilmelding.ikke-akseptert'));
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
        const errorMessages = this.getErrorMessages();
        const validationErrorClass = errorMessages.length === 0 ? '' : 'validation-error error';
        const validationMessages = this.props.showInline ? this.getErrorElements('span.checkbox-feilmelding', '-inline') : null;

        return (
            <div className="betingelsevalgpanel">
                <div className="nav-input">
                    <span className="vekk" role="alert" aria-live="assertive" aria-atomic="true">{this.state.ariastate}</span>
                    <input type="checkbox" name="betingelseValg:betingelserCheckbox" className="nav-checkbox" id="betingelser"
                      onChange={this.onChange} onBlur={this.onBlur} checked={this.state.checked}
                      aria-invalid={!this.isValid()} aria-describedby={this.getErrorElementId('-inline')}
                    />
                    <label htmlFor="betingelser" className={`checkboxgruppe ${validationErrorClass}`}>
                        <span className="typo-infotekst">{Resources.get('send-sporsmal.still-sporsmal.betingelser.sjekkboks')}</span>
                        <a href="#" className="typo-infotekst" onClick={this.visbetingelser}>{Resources.get('send-sporsmal.still-sporsmal.betingelser.vis')}</a>
                    </label>
                    <Betingelser ref="betingelser-panel" godta={this.betingelseCallback.bind(this, true)} avbryt={this.betingelseCallback.bind(this, false)}/>
                    {validationMessages}
                </div>
            </div>
        );
    }
});

export default GodtaVilkar;
