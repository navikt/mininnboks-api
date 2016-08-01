import React, { PropTypes as PT } from 'react';
import { FormattedMessage, FormattedHTMLMessage } from 'react-intl';
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
        const { godkjennVilkaar, avbryt, visModal, lukkModal } = this.props;

        /* eslint-disable no-script-url */
        return (
            <Modal
                modalConfig={modalConfig}
                visModal={visModal}
                lukkModal={lukkModal}
                className="react-modal-container mininnboks-modal"
            >
                <form onSubmit={this.submit} className="betingelser-panel panel side-innhold">
                    <h1 className="typo-sidetittel text-center blokk-l" tabIndex="0">
                        <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.overskrift" />
                    </h1>
                    <div className="blokk-m">
                        <FormattedHTMLMessage id="send-sporsmal.still-sporsmal.betingelser.tekst" />
                    </div>
                    <hr className="blokk-m" />
                    <div className="svar-godta blokk-center blokk-m">
                        <button
                            type="submit"
                            className="knapp knapp-hoved knapp-stor"
                            aria-controls="betingelser"
                            onClick={godkjennVilkaar}
                        >
                            <FormattedMessage id="send-sporsmal.still-sporsmal.betingelser.godta" />
                        </button>
                    </div>
                    <div className="text-center">
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
        );
    }
}

Betingelser.propTypes = {
    godkjennVilkaar: PT.func.isRequired,
    avbryt: PT.func.isRequired,
    visModal: PT.bool.isRequired,
    lukkModal: PT.func.isRequired
};

export default Betingelser;
