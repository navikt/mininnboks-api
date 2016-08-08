import React, { PropTypes as PT } from 'react';
import { hentLedetekster } from './ducks/ledetekster';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { IntlProvider, addLocaleData } from 'react-intl';
import nb from 'react-intl/locale-data/nb';
import classnames from 'classnames';
import Innholdslaster from './innholdslaster/innholdslaster';
import DevTools from './devtools';

addLocaleData(nb);

const cls = (visHeaderLevel) => classnames({
    'header-level': visHeaderLevel
});

class Application extends React.Component {
    componentWillMount() {
        this.props.actions.hentLedetekster();
    }

    render() {
        const visHeaderLevel = !!this.props.location.query.headerlevel;
        const { ledetekster = {}, children } = this.props;

        return (
            <div>
                <IntlProvider defaultLocale="nb" locale="nb" messages={ledetekster.data} >
                    <Innholdslaster avhengigheter={[ledetekster]}>
                        <div className={cls(visHeaderLevel)}>{children}</div>
                    </Innholdslaster>
                </IntlProvider>
                <div aria-hidden="true">
                    <DevTools />
                </div>
            </div>
        );
    }
}

Application.propTypes = {
    actions: PT.shape({
        hentLedetekster: PT.func
    }).isRequired,
    children: PT.object.isRequired,
    ledetekster: PT.object,
    location: PT.shape({
        query: PT.shape({
            headerlevel: PT.object
        })
    })
};

const mapStateToProps = ({ ledetekster }) => ({ ledetekster });
const mapDispatchToProps = (dispatch) => ({ actions: bindActionCreators({ hentLedetekster }, dispatch) });

export default connect(mapStateToProps, mapDispatchToProps)(Application);
