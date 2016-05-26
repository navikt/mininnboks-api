import React from 'react';

var ModalPortal = React.createClass({
    focusAfterClose: undefined,

    getDefaultProps() {
        return {
            skipFocus: ['div'],
            isOpen: false
        };
    },

    getInitialState() {
        return {
            title: createAriaOptional('title', this.props.title),
            description: createAriaOptional('description', this.props.description),
            closeButton: createAriaOptional('closeButton', this.props.closeButton)
        }
    },

    componentDidMount() {
        if (this.props.isOpen === true) {
            this.focusFirst();
        }
    },

    componentDidUpdate() {
        if (this.props.isOpen) {
            $(document.body).addClass('modal-open');
            $(document.body).children().not(this.getDOMNode().parentNode).attr('aria-hidden', true);

            if (!$.contains(this.refs.content.getDOMNode(), document.activeElement)) {
                this.focusFirst();
            }
        } else {
            $(document.body).children().not(this.getDOMNode().parentNode).removeAttr('aria-hidden');
            this.restoreFocus();
            $(document.body).removeClass('modal-open');
        }
    },

    keyHandler(event) {
        const keyMap = {
            27: function escHandler() { // ESC
                this.props.modal.close();
                event.preventDefault();
            },
            9: function tabHandler() { // TAB
                if (this.handleTab(event.shiftKey)) {
                    event.preventDefault();
                }
            }
        };

        (keyMap[event.keyCode] || function () {
        }).bind(this)();

        // No leaks
        event.stopPropagation();
    },

    handleTab(isShiftkey) {
        const $content = $(this.refs.content.getDOMNode());
        const focusable = $content.find(':tabbable');
        const lastValidIndex = isShiftkey ? 0 : focusable.length - 1;


        const currentFocusElement = $content.find(':focus');

        if (focusable.eq(lastValidIndex).is(currentFocusElement)) {
            const newFocusIndex = isShiftkey ? focusable.length - 1 : 0;
            focusable.eq(newFocusIndex).focus();
            return true;
        }
        return false;
    },

    focusFirst() {
        this.focusAfterClose = document.activeElement;
        let tabbables = $(this.refs.content.getDOMNode()).find(':tabbable');
        this.props.skipFocus.forEach(function (skipFocusTag) {
            tabbables = tabbables.not(skipFocusTag);
        });

        if (tabbables.length > 0) {
            tabbables.eq(0).focus();
        }
    },

    restoreFocus() {
        if (this.focusAfterClose) {
            this.focusAfterClose.focus();
            this.focusAfterClose = undefined;
        }
    },

    render() {
        let children = this.props.children;
        if (!children.hasOwnProperty('length')) {
            children = [children];
        }

        children.map(function (child) {
            return React.cloneElement(child, {
                modal: this.props.modal
            });
        }.bind(this));

        const title = this.state.title;
        const description = this.state.description;
        let closeButton = null;
        if (this.props.closeButton.show) {
            closeButton = (
                <button className="closeButton" onClick={this.props.modal.close}>
                    {this.state.closeButton.visible}
                </button>
            );
        }

        const cls = this.props.isOpen ? '' : 'hidden';
        return (
            <div tabIndex="-1" className={cls} aria-hidden={!this.props.isOpen} onKeyDown={this.keyHandler}
              role="dialog" aria-labelledby={title.id} aria-describedby={description.id}>
                <div className="backdrop" onClick={this.props.modal.close}></div>
                    {title.hidden}
                    {description.hidden}
                <div className="centering">
                    <div className="content" ref="content">
                        {title.visible}
                        {description.visible}
                        {children}
                        {closeButton}
                    </div>
                </div>
            </div>
        );
    }
});

function createId(prefix) {
    return `${prefix}${new Date().getTime()}-${Math.random()}`;
}

function createAriaOptional(name, data) {
    const id = createId('react-modalx-' + name + '-');
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


export default ModalPortal;
