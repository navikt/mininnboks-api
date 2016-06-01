import React from 'react';
import { hentInitData } from './utils/init/initActions.js';
import { connect } from 'react-redux';
import { IntlProvider, addLocaleData } from 'react-intl';
import Snurrepipp from './snurrepipp/Snurrepipp';
import nb from 'react-intl/dist/locale-data/nb';

addLocaleData(nb);

const renderApplication = (children, defaultTekster, headerlevel) => (
    <IntlProvider defaultLocale="nb" locale="nb" messages={defaultTekster} >
        <div className={headerlevel ? 'header-level' : null}>{ children }</div>
    </IntlProvider>
);

class Application extends React.Component {
    getChildContext() {
        return {
            tekster: this.props.tekster
        };
    }

    componentWillMount() {
        const { dispatch, location: { query: { cmskeys } } } = this.props;
        dispatch(hentInitData({
            cmskeys: !!cmskeys
        }));
    }

    render() {
        const headerlevel = !!this.props.location.query.headerlevel;
        const { harHentetInitData, children, tekster } = this.props;

        return harHentetInitData ? renderApplication(children, tekster, headerlevel) : <Snurrepipp />;
    }
}

Application.childContextTypes = {
    tekster: React.PropTypes.object
};

Application.propTypes = {
    dispatch: React.PropTypes.func.isRequired,
    harHentetInitData: React.PropTypes.bool.isRequired,
    children: React.PropTypes.object.isRequired,
    tekster: React.PropTypes.object,
    location: React.PropTypes.shape({
        query: React.PropTypes.shape({
            headerlevel: React.PropTypes.object
        })
    })
};

export default connect(({ harHentetInitData, tekster }) => ({ harHentetInitData, tekster }))(Application);
