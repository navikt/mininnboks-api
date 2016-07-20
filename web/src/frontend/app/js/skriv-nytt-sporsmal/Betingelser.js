import React, { PropTypes as pt } from 'react';
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
        const { formatMessage, godkjennVilkaar, avbryt, visModal, lukkModal } = this.props;

        return (
            <div className="react-modal-container">
                <Modal modalConfig={modalConfig} visModal={visModal} lukkModal={lukkModal} >
                    <form onSubmit={this.submit} className="betingelser-panel side-innhold">
                        <h1 className="typo-sidetittel text-center blokk-l" tabIndex="0">{formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.overskrift' })}</h1>
                        <div dangerouslySetInnerHTML={{ __html: formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.tekst' }) }}></div>
                        <hr />
                        <input type="submit" className="svar-godta knapp knapp-hoved knapp-stor" aria-controls="betingelser" onClick={godkjennVilkaar}
                          value={formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.godta' })}
                        />
                        <div className="avbryt">
                            <a href="javascript:void(0)" onClick={avbryt}
                              aria-controls="betingelser" role="button"
                            >
                                {formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.ikke-godta' })}
                            </a>
                        </div>
                    </form>
                </Modal>
            </div>
        );
    }
}

Betingelser.propTypes = {
    formatMessage: pt.func.isRequired,
    godkjennVilkaar: pt.func.isRequired,
    avbryt: pt.func.isRequired,
    visModal: pt.bool.isRequired,
    lukkModal: pt.func.isRequired
};

export default Betingelser;
