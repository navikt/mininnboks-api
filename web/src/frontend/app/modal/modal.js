import React, { PropTypes as PT } from 'react';
// eslint-disable-next-line camelcase
import { unstable_renderSubtreeIntoContainer as renderSubtreeIntoContainer, unmountComponentAtNode } from 'react-dom';
import Portal from './modal-portal';

class Modal extends React.Component {
    constructor(props) {
        super(props);
        this.renderPortal = this.renderPortal.bind(this);
    }

    componentDidMount() {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = this.props.className || 'react-modal-container';
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props);
    }

    componentWillReceiveProps(props) {
        this.renderPortal(props);
    }

    componentDidUpdate() {
        this.renderPortal(this.props);
    }

    componentWillUnmount() {
        unmountComponentAtNode(this.portalElement);
    }

    renderPortal(props) {
        renderSubtreeIntoContainer(this, <Portal {...props} />, this.portalElement);
    }

    render() {
        return null;
    }
}

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

Modal.propTypes = {
    className: PT.string
};

export default Modal;
