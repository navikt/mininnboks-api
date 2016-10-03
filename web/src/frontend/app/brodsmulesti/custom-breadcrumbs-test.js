/* eslint-env mocha */
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import Breadcrumbs from './custom-breadcrumbs';

const renderThenFind = (component, selector) => () => {
    const rendered = shallow(component);

    const wrappingComponent = rendered.find(selector);
    // console.log('found', wrappingComponent.debug());
    expect(wrappingComponent).to.have.length(1);
};

describe('Breadcrumbs', () => {
    const defaultRoute = {
        breadcrumbName: 'breadcrumbName',
        path: 'path',
        component: { name: 'componentname' }
    };
    const simpleRoute = [defaultRoute];

    it('skal ikke vises på mobil', renderThenFind(<Breadcrumbs routes={simpleRoute} />, 'div.vekk-mobil'));
    it('skal ha en skjult overskrift for skjemlesere', renderThenFind(<Breadcrumbs routes={simpleRoute} />, 'h2'));
    it('skal ha en skjult overskrift for skjemlesere', renderThenFind(<Breadcrumbs routes={simpleRoute} />, '.vekk'));
    it('skal ha en skjult overskrift for skjemlesere', () => {
        const rendered = shallow(<Breadcrumbs routes={simpleRoute} />);

        const header = rendered.find('h2');
        expect(header.hasClass('vekk')).to.equal(true);// Fordi find('h2.vekk) feiler
    });
});
