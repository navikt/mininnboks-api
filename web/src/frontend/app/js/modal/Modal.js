import React, { PropTypes as pt } from 'react';
import Portal from './ModalPortal.js';
import { render } from 'react-dom';
import { IntlProvider } from 'react-intl';

class Modal extends React.Component {
    constructor(props) {
        super(props);
        this.renderPortal = this.renderPortal.bind(this);
    }

    componentDidMount() {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = 'react-modal-container';
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props, this.context.tekster);
    }

    componentWillReceiveProps(props) {
        this.renderPortal(props);
    }

    componentDidUpdate() {
        this.renderPortal(this.props);
    }

    componentWillUnmount() {
        document.body.removeChild(this.portalElement);
    }

    renderPortal(props, tekster) {
        this.modal = render(
            (
                <IntlProvider defaultLocale="nb" locale="nb" messages={tekster}>
                    <Portal {...props} />
                </IntlProvider>
            ), this.portalElement);
    }

    render() {
        return null;
    }
}

Modal.contextTypes = {
    tekster: pt.object
};

Modal.defaultProps = {
    modalConfig: {
        title: {
            text: 'Modal Title',
            show: false,
            tag: 'h1'
        },
        description: {
            text: '',
            show: false,
            tag: 'div'
        },
        closeButton: {
            text: '',
            show: true,
            tag: 'span'
        }
    }
};

export default Modal;
