import React from 'react';
import Portal from './ModalPortal.js';

class Modal extends React.Component {
    
    constructor(props) {
        super(props);
        this.state = { isOpen: this.props.isOpen || false };
        this.open = this.open.bind(this);
        this.close = this.close.bind(this);
        this.renderPortal = this.renderPortal.bind(this);
    }
    
    componentDidMount () {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = "react-modal-container";
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props, this.state);
    }
    
    componentWillReceiveProps (props) {
        this.renderPortal(props, this.state)
    }
    
    componentWillUnmount () {
        document.body.removeChild(this.portalElement);
    }
    
    componentDidUpdate (){
        this.renderPortal(this.props, this.state)
    }
    
    open () {
        this.setState({isOpen: true});
    }
    
    close () {
        this.setState({isOpen: false});
    }
    
    renderPortal (props, state) {
        var modal = {
            open: this.open,
            close: this.close
        };

        this.modal = React.render(<Portal {...props} {...state} modal={modal} />, this.portalElement);
    }
    
    render () {
        return null;
    }
};

Modal.defaultProps = {
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
};

export default Modal;
