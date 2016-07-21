import './console-polyfill';
import './utils/init/globals';
import React from 'react';
import { render } from 'react-dom';
import Router from './router';
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux';
import thunkMiddleware from 'redux-thunk';
import mainReducer from './utils/reducers/reducer';

const store = applyMiddleware(thunkMiddleware)(createStore)(mainReducer);
document.addEventListener('DOMContentLoaded', () => {
    render(<Provider store={store}><Router /></Provider>, document.getElementById('mainapp'));
});
