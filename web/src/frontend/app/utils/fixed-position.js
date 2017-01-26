import React, { Component, PropTypes as PT } from 'react';
import { autobind, throttle, debounce } from '../utils';

class FixedPosition extends Component {
    constructor(props) {
        super(props);

        autobind(this);
        this.setFixed = debounce(this.setFixed, 300);
    }

    componentDidMount() {
        this.handler = throttle(this.handleResize, 100);
        window.addEventListener('resize', this.handler);
        this.setFixed();
    }

    componentWillUnmount() {
        window.addEventListener('resize', this.handler);
    }

    setStyle(position, top, left) {
        this.refs.fixed.style.position = position;
        if (top) {
            this.refs.fixed.style.top = top;
        } else {
            this.refs.fixed.style.removeProperty('top');
        }
        if (left) {
            this.refs.fixed.style.left = left;
        } else {
            this.refs.fixed.style.removeProperty('left');
        }
    }

    setFixed() {
        const boundingRect = this.refs.fixed.getBoundingClientRect();
        const scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);

        this.setStyle('fixed', `${scrollTop + boundingRect.top}px`, `${boundingRect.left}px`);
    }

    handleResize() {
        this.setStyle('absolute', undefined, undefined);
        setTimeout(() => this.setFixed(), 0);
    }

    render() {
        const { children, ...props } = this.props;
        return (
            <div {...props} className="fixed-position js-fixed-position" ref="fixed">
                {children}
            </div>
        );
    }
}

FixedPosition.propTypes = {
    children: PT.node.isRequired
};

export default FixedPosition;
