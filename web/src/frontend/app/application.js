import React, { PropTypes as PT } from 'react';
import { hentInitData } from './utils/init/init-actions';
import { connect } from 'react-redux';
import { IntlProvider, addLocaleData } from 'react-intl';
import nb from 'react-intl/locale-data/nb';
import classnames from 'classnames';
import Spinner from './utils/Spinner';

addLocaleData(nb);

const cls = (visHeaderLevel) => classnames({
    'header-level': visHeaderLevel
});

class Application extends React.Component {
    componentWillMount() {
        const { location: { query: { cmskeys } } } = this.props;
        this.props.hentInitData({ cmskeys: !!cmskeys });
    }

    render() {
        const visHeaderLevel = !!this.props.location.query.headerlevel;
        const { harHentetInitData, children, tekster } = this.props;

        if (!harHentetInitData) {
            return <Spinner />;
        }

        return (
            <IntlProvider defaultLocale="nb" locale="nb" messages={tekster} >
                <div className={cls(visHeaderLevel)}>{children}</div>
            </IntlProvider>
        );
    }
}

Application.propTypes = {
    harHentetInitData: PT.bool.isRequired,
    hentInitData: PT.func.isRequired,
    children: PT.object.isRequired,
    tekster: PT.object,
    location: PT.shape({
        query: PT.shape({
            headerlevel: PT.object
        })
    })
};

const mapStateToProps = ({ data: { harHentetInitData, tekster } }) => ({ harHentetInitData, tekster });
const mapDispatchToProps = (dispatch) => ({
    hentInitData: (options) => dispatch(hentInitData(options))
});

export default connect(mapStateToProps, mapDispatchToProps)(Application);
