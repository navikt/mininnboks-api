import React from 'react';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import FeedbackForm from '../feedback/FeedbackForm';

class BesvarBoks extends React.Component {
    constructor(props) {
        super(props);
        this.state = {sender: false};
        this.onSubmit = this.onSubmit.bind(this);
        this.skjul = this.skjul.bind(this);
    }

    onSubmit (evt) {
        evt.preventDefault();

        var form = this.refs.form;
        if (form.isValid()) {
            var submitPromise = this.props.besvar(form.getFeedbackRef('textarea').getInput());
            this.setState({sender: true});

            if (!submitPromise) {
                this.setState({sender: false})
            } else {
                submitPromise.always(function () {
                    this.setState({sender: false})
                }.bind(this));
            }
        }
    }

    skjul (event) {
        event.preventDefault();
        this.props.skjul();
    }

    render () {
        if (!this.props.vis) {
            return null;
        }

        var knapper;
        if (this.state.sender) {
            knapper = <Snurrepipp storrelse="48" farge="graa"/>;
        } else {
            knapper = (
                <div>
                    <input type="submit" className="knapp-hoved-liten"
                           value={this.props.resources.get('traadvisning.besvar.send')} onClick={this.onSubmit}/>

                    <p>
                        <a href="#" onClick={this.skjul}
                           role="button">{this.props.resources.get('traadvisning.besvar.avbryt')}</a>
                    </p>
                </div>
            );
        }

        return (
            <FeedbackForm className="besvar-container" ref="form">
                <ExpandingTextArea placeholder={this.props.resources.get('traadvisning.besvar.tekstfelt.placeholder')}
                                   charsLeftText={this.props.resources.get('traadvisning.besvar.tekstfelt.tegnigjen')}
                                   feedbackref="textarea"/>
                {knapper}
            </FeedbackForm>
        );
    }
};

export default BesvarBoks;