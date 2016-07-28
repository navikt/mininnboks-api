import React, { Component, PropTypes as PT } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { reduxForm } from 'redux-form';
import { revokeSubmittoken } from './nav-form-actions';

export function createForm(component, formConfig = {}, initialData = {}) {
    class NAVForm extends Component {
        componentWillMount() {
            this.props.initializeForm(initialData);
        }

        componentWillUpdate(nextProps) {
            if (nextProps.valid && !this.props.valid) {
                this.props.NAVFormAction.revokeSubmittoken();
            }
        }

        render() {
            return React.createElement(component, this.props);
        }
    }

    NAVForm.propTypes = {
        submitToken: PT.string,
        NAVFormAction: PT.shape({
            revokeSubmittoken: PT.func.isRequired
        }).isRequired,
        initializeForm: PT.func.isRequired,
        valid: PT.bool.isRequired
    };

    const mapStateToProps = ({ form }) => ({
        submitToken: form[formConfig.form].submitToken
    });
    const mapDispatchToProps = (dispatch) => ({
        NAVFormAction: bindActionCreators({ revokeSubmittoken }, dispatch)
    });

    return connect(mapStateToProps, mapDispatchToProps)(reduxForm(formConfig)(NAVForm));
}
