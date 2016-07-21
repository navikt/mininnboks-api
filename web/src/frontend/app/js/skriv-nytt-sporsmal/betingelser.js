import React, { PropTypes as PT } from 'react';
import { injectIntl, FormattedMessage } from 'react-intl';
import Modal from '../modal/modal';

const modalConfig = {
    title: {
        text: 'Betingelser modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk modal',
        show: true,
        tag: 'span.vekk'
    }
};

class Betingelser extends React.Component {

    submit(e) {
        e.preventDefault();
    }

    render() {
        const { godkjennVilkaar, avbryt, visModal, lukkModal, intl } = this.props;

        const htmlContent = intl.formatMessage({id: 'send-sporsmal.still-sporsmal.betingelser.tekst'});

        /* eslint-disable no-script-url */
        return (
            <div className="react-modal-container">
                <Modal modalConfig={modalConfig} visModal={visModal} lukkModal={lukkModal} >
                    <form onSubmit={this.submit} className="betingelser-panel side-innhold">
                        <h1 className="typo-sidetittel text-center blokk-l" tabIndex="0">
                            <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.overskrift" />
                        </h1>
                        <div dangerouslySetInnerHTML={{ __html: htmlContent }} />
                        <hr />
                        <button
                            type="submit"
                            className="svar-godta knapp knapp-hoved knapp-stor"
                            aria-controls="betingelser"
                            onClick={godkjennVilkaar}
                        >
                            <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.godta" />
                        </button>
                        <div className="avbryt">
                            <a
                                href="javascript:void(0)"
                                onClick={avbryt}
                                aria-controls="betingelser"
                                role="button"
                            >
                                <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.ikke-godta" />
                            </a>
                        </div>
                    </form>
                </Modal>
            </div>
        );
    }
}

Betingelser.propTypes = {
    intl: PT.object.isRequired,
    godkjennVilkaar: PT.func.isRequired,
    avbryt: PT.func.isRequired,
    visModal: PT.bool.isRequired,
    lukkModal: PT.func.isRequired
};

export default injectIntl(Betingelser);
