/* eslint-env mocha */
// import { expect, React } from '../../../test-helper';
// import { shallow } from 'enzyme';
// import Innholdslaster from './innholdslaster';
// import Laster from './innholdslaster-laster';
// import { STATUS } from '../../felles/konstanter';
//
// describe('Innholdslaster', () => {
//     it('skal vise laster om noen laster', () => {
//         const laster1 = { status: STATUS.laster, data: {} };
//         const laster2 = { status: STATUS.laster, data: {} };
//         const lastet = { status: STATUS.lastet, data: {} };
//
//         const wrapper = shallow(<Innholdslaster avhengigheter={[laster1, laster2, lastet]} />);
//         expect(wrapper.equals(<Laster />)).to.equal(true);
//     });
//
//     it('skal vise feilmelding om noe feilet', () => {
//         const feil = { status: STATUS.feilet, data: { response: { status: 500 } } };
//         const lastet = { status: STATUS.lastet, data: {} };
//         const lastet2 = { status: STATUS.lastet, data: {} };
//
//         const wrapper = shallow(<Innholdslaster avhengigheter={[lastet, feil, lastet2]} />);
//         expect(wrapper.is('Feil')).to.equal(true);
//     });
//
//     it('skal vise child om alt gikk greit', () => {
//         const lastet = { status: STATUS.lastet, data: {} };
//         const lastet2 = { status: STATUS.lastet, data: {} };
//
//         const wrapper = shallow((
//             <Innholdslaster avhengigheter={[lastet, lastet2]}>
//                 DETTE SKAL VISES
//             </Innholdslaster>)
//         );
//         expect(wrapper.text()).to.equal('DETTE SKAL VISES');
//     });
// });
