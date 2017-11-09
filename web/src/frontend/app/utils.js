import React from 'react';
import sanitize from 'sanitize-html';
import moment from 'moment';
import 'moment/locale/nb';
import Constants from './utils/constants';

moment.locale('nb');

export const leggTilLenkerTags = (innhold) => {
    const uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
    const httpRegex = /^(https?):\/\/.*$/;

    return innhold.replace(uriRegex, (match) => {
        const matched = match.match(httpRegex) ? match : `http://${match}`;
        return `<a target="_blank" href="${matched}">${matched}</a>`;
    });
};

export const safeHtml = (content) => sanitize(content, { allowedTags: ['a'] });

export const tilAvsnitt = (avsnitt, index) => (
    <p className="sanitized-content" dangerouslySetInnerHTML={{ __html: safeHtml(avsnitt) }} key={index} />
);


export const prettyDate = (date) => moment(date).format('Do MMMM YYYY, [kl.] HH:mm');

export const shortDate = (date) => moment(date).format('DD.MM.YY');

export const status = (melding) => {
    if (melding.type === 'SVAR_SBL_INNGAAENDE') {
        return Constants.BESVART;
    } else if (!melding.lest) {
        return Constants.IKKE_LEST;
    } else if (melding.type === 'SPORSMAL_MODIA_UTGAAENDE') {
        return Constants.LEST_UBESVART;
    }
    return Constants.LEST;
};

export function nyesteTraadForst(traad1, traad2) {
    const d1 = new Date(traad1.nyeste.opprettet);
    const d2 = new Date(traad2.nyeste.opprettet);

    if (d1 < d2) return 1;
    else if (d1 > d2) return -1;
    return 0;
}

export function eldsteMeldingForst(melding1, melding2) {
    const d1 = new Date(melding1.opprettet);
    const d2 = new Date(melding2.opprettet);

    if (d1 > d2) {
        return 1;
    } else if (d1 < d2) {
        return -1;
    }
    return 0;
}

export const reduxFormProps = ({
    checked, name, onBlur, onChange, onDragStart, onDrop, onFocus, value
}) => ({ checked, name, onBlur, onChange, onDragStart, onDrop, onFocus, value });

export function autobind(ctx) {
    Object.getOwnPropertyNames(ctx.constructor.prototype)
        .filter((prop) => typeof ctx[prop] === 'function')
        .forEach((method) => {
            // eslint-disable-next-line
            ctx[method] = ctx[method].bind(ctx);
        });
}

export function debounce(func, wait, immediate) {
    let timeout;
    return function debounced(...args) {
        const context = this;
        const later = () => {
            timeout = null;
            if (!immediate) {
                func.apply(context, args);
            }
        };
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) {
            func.apply(context, args);
        }
    };
}

export const fn = (value) => (typeof value === 'function' ? value : () => value);
export const getDisplayName = (component) => component.displayName || component.name || 'Component';

export function throttle(func, threshold = 250) {
    let last;
    let deferTimer;

    return function throttled(...args) {
        const context = this;

        const now = +new Date();
        if (last && now < last + threshold) {
            clearTimeout(deferTimer);
            deferTimer = setTimeout(() => {
                last = now;
                func.apply(context, args);
            }, threshold);
        } else {
            last = now;
            func.apply(context, args);
        }
    };
}

export function erDev() {
    const url = window.location.href;
    return url.includes('debug=true') || url.includes('devillo.no:8586') || url.includes('localhost:8586');
}
