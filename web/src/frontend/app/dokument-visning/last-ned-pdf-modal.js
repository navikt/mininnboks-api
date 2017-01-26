/* eslint-disable no-script-url */
import React from 'react';
import { connect } from 'react-redux';
import { injectIntl, intlShape } from 'react-intl';
import AriaModal from 'react-aria-modal';
import { skjulLastNedPdfModal } from './../ducks/dokumenter.js';


class Modal extends React.Component {
    constructor() {
        super();
        this.deactivateModal = this.deactivateModal.bind(this);
    }

    deactivateModal() {
        const { actions } = this.props;
        actions.skjulLastNedPdfModal();
    }

    render() {
        const { pdfModal, intl: { formatMessage } } = this.props;
        return (
            <AriaModal
                mounted={pdfModal.skalVises}
                titleText={formatMessage({ id: 'modal.lastnedpdf.aria.tittel' })}
                onExit={this.deactivateModal}
                applicationNode={document.getElementById('modal-guard')}
            >
                <div className="mininnboks-modal side-innhold last-ned-pdf-modal">
                    <div className="modal-bakteppe" ></div>
                    <div className="modal-alert modal-vindu">
                        <div className="panel">
                            <h2 className="hode hode-innholdstittel hode-dekorert hode-advarsel blokk-s" >
                                <span className="vekk">{formatMessage({ id: 'modal.lastnedpdf.ikon.aria' })}</span>
                                {formatMessage({ id: 'modal.lastnedpdf.obs' })}
                            </h2>
                            <p className="blokk-s text-center">{formatMessage({ id: 'modal.lastnedpdf.tekst' })}</p>
                            <div className="knapperad knapperad-adskilt">
                                <div className="blokk-s">
                                    <a
                                        role="button"
                                        target="_blank"
                                        href={pdfModal.dokumentUrl}
                                        className="knapp knapp-hoved"
                                        id="first-button"
                                        onClick={this.deactivateModal}
                                    >
                                        {formatMessage({ id: 'modal.lastnedpdf.fortsett' })}
                                    </a>
                                </div>
                                <a role="button" href="javascript:void(0)" onClick={this.deactivateModal}>
                                    {formatMessage({ id: 'modal.lastnedpdf.lukk' })}
                                </a>
                            </div>
                            <button type="button" className="modal-lukk" onClick={this.deactivateModal}>
                                {formatMessage({ id: 'modal.lastnedpdf.aria.lukkekryss' })}
                            </button>
                        </div>
                    </div>
                </div>
            </AriaModal>
        );
    }
}

Modal.propTypes = {
    dispatch: React.PropTypes.func.isRequired,
    pdfModal: React.PropTypes.object.isRequired,
    actions: React.PropTypes.object.isRequired,
    intl: intlShape
};

const mapStateToProps = ({ dokumenter }) => ({ pdfModal: dokumenter.pdfModal });
const mapDispatchToProps = (dispatch) => ({
    actions: {
        skjulLastNedPdfModal: () => dispatch(skjulLastNedPdfModal())
    }
});
const connectedComponent = connect(mapStateToProps, mapDispatchToProps)(Modal);

export default injectIntl(connectedComponent);
