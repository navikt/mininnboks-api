import './console-polyfill';
import './utils/init/Globals';
import React from 'react';
import { render } from 'react-dom';
import Router from './Router';
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux';
import thunkMiddleware from 'redux-thunk';
import mainReducer from './utils/reducers/Reducer';

const store = applyMiddleware(thunkMiddleware)(createStore)(mainReducer);
document.addEventListener('DOMContentLoaded', () => {
    render(<Provider store={store}><Router/></Provider>, document.getElementById('mainapp'));
});
