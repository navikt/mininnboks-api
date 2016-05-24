import React from 'react/addons';
import ValidatableMixin from '../feedback/ValidatableMixin';
import Resources from '../resources/Resources';
import Betingelser from './Betingelser';

var GodtaVilkar = React.createClass({
    mixins: [ValidatableMixin],
    getInitialState: function () {
        return {checked: false};
    },
    onChange: function (event) {
        var checked = event.target.checked;
        this.setState({checked: checked, ariastate: 'Checkbox '+(checked ? 'avkrysset' : 'ikke avkrysset')});
        this.validate(checked);
    },
    onBlur: function () {
        this.validate();
    },
    validate: function (checked) {
        if (typeof checked === 'undefined') {
            checked = this.state.checked;
        }
        if (checked) {
            this.valid();
        } else {
            this.error(Resources.get('send-sporsmal.still-sporsmal.betingelser.feilmelding.ikke-akseptert'));
        }
    },
    visbetingelser: function (e) {
        this.refs.betingelserPanel.vis();
        e.preventDefault();
    },
    betingelseCallback: function (status) {
        this.refs.betingelserPanel.skjul();
        this.onChange({target: {checked: status}});
    },
    render: function () {
        var errorMessages = this.getErrorMessages();
        var validationErrorClass = errorMessages.length === 0 ? '' : 'validation-error error';
        var validationMessages = this.props.showInline ? this.getErrorElements('span.checkbox-feilmelding', '-inline') : null;
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
                        <span>{Resources.get("send-sporsmal.still-sporsmal.betingelser.sjekkboks")}</span>
                        <a href="#" className="vilkarlenke" onClick={this.visbetingelser}>{Resources.get("send-sporsmal.still-sporsmal.betingelser.vis")}</a>
                    </label>
                    <Betingelser ref="betingelserPanel" godta={this.betingelseCallback.bind(this, true)} avbryt={this.betingelseCallback.bind(this, false)}/>
                    {validationMessages}
                </div>
            </div>
        );
    }
});

module.exports = GodtaVilkar;