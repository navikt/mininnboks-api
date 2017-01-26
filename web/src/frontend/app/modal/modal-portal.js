import React, { PropTypes as PT } from 'react';
import classNames from 'classnames';

const createId = (prefix) => `${prefix}${new Date().getTime()}-${Math.random()}`;

function createAriaOptional(name, data) {
    const id = createId(`react-modalx-${name}-`);
    const tagComponent = data.tag.split('.');
    const tagType = tagComponent[0];
    let className = '';

    if (tagComponent.length > 1) {
        className = tagComponent[1];
    }
    const element = React.createElement(tagType, { id, className }, data.text);

    return {
        id,
        hidden: data.show ? null : element,
        visible: data.show ? element : null
    };
}

class ModalPortal extends React.Component {

    componentDidMount() {
        if (this.props.visModal) {
            this.focusFirst();
        }
    }

    componentDidUpdate() {
        if (this.props.visModal) {
            document.body.classList.add('modal-open');

            const documentChildren = [].slice.call(document.body.children);
            documentChildren.forEach(child => {
                if (!child.classList.contains('react-modal-container')) {
                    child.setAttribute('aria-hidden', 'true');
                }
            });

            this.focusFirst();
        } else {
            document.body.classList.remove('modal-open');

            [].slice.call(document.body.children).forEach(child => child.removeAttribute('aria-hidden'));

            this.restoreFocus();
        }
    }

    handleTab(isShiftkey) {
        const $content = $(this.refs.content);
        const focusable = $content.find(':tabbable');
        const lastValidIndex = isShiftkey ? 0 : focusable.length - 1;

        const currentFocusElement = $content.find(':focus');

        if (focusable.eq(lastValidIndex).is(currentFocusElement)) {
            const newFocusIndex = isShiftkey ? focusable.length - 1 : 0;
            focusable.eq(newFocusIndex).focus();
            return true;
        }
        return false;
    }

    keyHandler(lukkModal) {
        return (event) => {
            const keyMap = {
                27: () => { // ESC
                    lukkModal();
                    event.preventDefault();
                },
                9: () => { // TAB
                    if (this.handleTab(event.shiftKey)) {
                        event.preventDefault();
                    }
                }
            };

            (keyMap[event.keyCode] || (() => {}))();

            // No leaks
            event.stopPropagation();
        };
    }


    focusFirst() {
        this.focusAfterClose = document.activeElement;
        let tabbables = $(this.refs.content).find(':tabbable');
        this.props.skipFocus.forEach((skipFocusTag) => {
            tabbables = tabbables.not(skipFocusTag);
        });

        if (tabbables.length > 0) {
            tabbables.eq(0).focus();
        }
    }

    restoreFocus() {
        if (this.focusAfterClose) {
            this.focusAfterClose.focus();
            this.focusAfterClose = undefined;
        }
    }

    render() {
        const { modalConfig: { title, description, closeButton }, visModal, lukkModal } = this.props;

        const ariaOptionalTitle = createAriaOptional('title', title);
        const ariaOptionalDescription = createAriaOptional('description', description);
        const ariaOptionalCloseButton = createAriaOptional('closeButton', closeButton);

        let children = this.props.children;
        if (!children.hasOwnProperty('length')) {
            children = [children];
        }

        children.map(child => React.cloneElement(child));

        const lukkButton = (
            <button className="closeButton" onClick={lukkModal} >
                {ariaOptionalCloseButton.hidden}
            </button>
        );

        const visEllerSkjulModal = classNames(this.props.className, {
            hidden: !visModal
        });
        return (
            <div
                tabIndex="-1"
                className={visEllerSkjulModal}
                aria-hidden={!visModal}
                onKeyDown={this.keyHandler(lukkModal)}
                role="dialog"
                aria-labelledby={title.id}
                aria-describedby={description.id}
            >
                <div className="backdrop" onClick={lukkModal} tabIndex="-1" role="presentation"></div>
                {ariaOptionalTitle.hidden}
                {ariaOptionalDescription.hidden}
                <div className="centering">
                    <div className="content" ref="content">
                        {ariaOptionalTitle.visible}
                        {ariaOptionalDescription.visible}
                        {children}
                        {lukkButton}
                    </div>
                </div>
            </div>
        );
    }
}

ModalPortal.defaultProps = {
    skipFocus: ['div'],
    isOpen: false
};

ModalPortal.propTypes = {
    children: PT.node.isRequired,
    className: PT.string,
    modalConfig: PT.shape({
        title: PT.object.isRequired,
        description: PT.object.isRequired,
        closeButton: PT.object.isRequired
    }).isRequired,
    skipFocus: PT.arrayOf(PT.string),
    visModal: PT.bool.isRequired,
    lukkModal: PT.func.isRequired
};

export default ModalPortal;
