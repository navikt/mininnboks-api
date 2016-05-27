import React from 'react';
import Modal from '../modal/Modal';
import Snurrepipp from '../snurrepipp/Snurrepipp';

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
    constructor(props) {
        super(props);
        this.state = { hentet: false };
        this.submit = this.submit.bind(this);
        this.vis = this.vis.bind(this);
        this.skjul = this.skjul.bind(this);
    }

    submit(e) {
        e.preventDefault();
    }

    vis() {
        this.refs.modal.open();
    }

    skjul() {
        this.refs.modal.close();
    }

    render() {
        const { formatMessage, godta, avbryt } = this.props;
        return (
            <Modal ref="modal" title={modalConfig.title} description={modalConfig.description} closeButton={modalConfig.closeButton}>
                <form onSubmit={this.submit} className="betingelserPanel">
                    <h1 className="stor" tabIndex="0">{formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.overskrift' })}</h1>
                    <div className="robust-strek"></div>
                    <div dangerouslySetInnerHTML={{ __html: formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.tekst' }) }}></div>
                    <hr />
                    <input type="submit" className="svar-godta knapp-hoved-stor"
                      onClick={godta} aria-controls="betingelser"
                      value={formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.godta' })}
                    />
                    <div className="avbryt">
                        <a href="javascript:void(0)" className="svar-avbryt" onClick={avbryt} 
                           aria-controls="betingelser" role="button"
                        >
                            {formatMessage({ id: 'send-sporsmal.still-sporsmal.betingelser.ikke-godta' })}
                        </a>
                    </div>
                </form>
            </Modal>
        );
    }
}

export default Betingelser;
