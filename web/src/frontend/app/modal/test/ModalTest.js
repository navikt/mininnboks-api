/* eslint-env mocha */
// import './../../testConfig';
// import { expect } from 'chai';
// import React from 'react';
// import Modal from '../Modal.js';
//
// import { findRenderedDOMComponentWithClass, renderIntoDocument } from 'react-addons-test-utils';
//
// function createModal(props, children) {
//     return renderIntoDocument(React.createElement(
//             Modal, props || {},
//             children || React.createElement('span', { className: 'forReference' }, 'test'))
//     );
// }
// function getContent(modal) {
//     return modal.modal.refs.content;
// }

describe('Modal', () => {
    //
    // afterEach(function (done) {
    //     React.unmountComponentAtNode(document.body);
    //     document.body.innerHTML = "";
    //     setTimeout(done);
    // });
    //
    // it('creates portal as a direct child of body', function () {
    //     var modal = createModal();
    //
    //     var modalContainer = document.getElementsByClassName('react-modal-container');
    //     expect(modalContainer).not.to.be.null;
    // });
    //
    // it('default is closed and have the correct attributes', function () {
    //     var modal = createModal();
    //     var portal = modal.modal.getDOMNode();
    //
    //     expect(portal.hasAttribute('tabindex')).to.be.true;
    //     expect(portal.hasAttribute('class')).to.be.true;
    //     expect(portal.hasAttribute('aria-hidden')).to.be.true;
    //     expect(portal.hasAttribute('role')).to.be.true;
    //     expect(portal.hasAttribute('aria-labelledby')).to.be.true;
    //     expect(portal.hasAttribute('aria-describedby')).to.be.true;
    //
    //     expect(portal.getAttribute('tabindex')).to.be.eql('-1');
    //     expect(portal.getAttribute('class')).to.be.eql('hidden');
    //     expect(portal.getAttribute('aria-hidden')).to.be.eql('true');
    //     expect(portal.getAttribute('role')).to.be.eql('dialog');
    //
    //     var labelledById = portal.getAttribute('aria-labelledby');
    //     var describedById = portal.getAttribute('aria-describedby');
    //
    //     var labelledBy = document.getElementById(labelledById);
    //     var describedBy = document.getElementById(describedById);
    //
    //     expect(labelledBy).not.to.be.null;
    //     expect(describedBy).not.to.be.null;
    // });
    //
    // it('repects the isOpen prop', function () {
    //     var modal = createModal({isOpen: true});
    //
    //     var portal = modal.modal.getDOMNode();
    //     expect(portal.getAttribute('class')).not.to.be.eql('hidden');
    // });
    //
    // it('Renders content til portal.content div', function () {
    //     var modal = createModal();
    //     var content = getContent(modal);
    //
    //     var span = findRenderedDOMComponentWithClass(content, 'forReference');
    //     expect(span).not.to.be.null;
    // });
});
