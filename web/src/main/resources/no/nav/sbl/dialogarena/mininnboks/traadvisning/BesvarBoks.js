import React, { PropTypes as pt } from 'react';
import ExpandingTextArea from '../expandingtextarea/ExpandingTextArea';
import Snurrepipp from '../snurrepipp/Snurrepipp';
import FeedbackForm from '../feedback/FeedbackForm';

class BesvarBoks extends React.Component {
    constructor(props) {
        super(props);
        this.state = { sender: false };
        this.onSubmit = this.onSubmit.bind(this);
        this.skjul = this.skjul.bind(this);
    }

    onSubmit(evt) {
        evt.preventDefault();

        var form = this.refs.form;
        if (form.isValid()) {
            const submitPromise = this.props.besvar(form.getFeedbackRef('textarea').getInput());
            this.setState({ sender: true });

            if (!submitPromise) {
                this.setState({ sender: false });
            } else {
                submitPromise.always(function () {
                    this.setState({ sender: false });
                }.bind(this));
            }
        }
    }

    skjul(event) {
        event.preventDefault();
        this.props.skjul();
    }

    render() {
        const { formatMessage, vis } = this.props;

        if (!vis) {
            return <noscript/>;
        }

        let knapper;
        if (this.state.sender) {
            knapper = <Snurrepipp storrelse="48" farge="graa"/>;
        } else {
            knapper = (
                <div>
                    <input type="submit" className="knapp knapp-hoved knapp-liten"
                      value={formatMessage({ id: 'traadvisning.besvar.send' })} onClick={this.onSubmit}
                    />

                    <p>
                        <a href="#" onClick={this.skjul} role="button">
                            {formatMessage({ id: 'traadvisning.besvar.avbryt' })}
                        </a>
                    </p>
                </div>
            );
        }

        return (
            <FeedbackForm className="besvar-container" ref="form">
                <ExpandingTextArea placeholder={formatMessage({ id: 'traadvisning.besvar.tekstfelt.placeholder'} )}
                  charsLeftText={formatMessage({ id: 'traadvisning.besvar.tekstfelt.tegnigjen' })}
                  infotekst=""
                  feedbackref="textarea"
                />
                {knapper}
            </FeedbackForm>
        );
    }
}

BesvarBoks.propTypes = {
    vis: pt.bool,
    skjul: pt.func,
    besvar: pt.func,
    formatMessage: pt.func.isRequired
};

export default BesvarBoks;
