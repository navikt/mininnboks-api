import React from 'react';
import sanitize from 'sanitize-html';
import moment from 'moment';
import 'moment/locale/nb';
import Constants from './constants';

moment.locale('nb');

export const leggTilLenkerTags = (innhold) => {
    const uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
    const httpRegex = /^(https?):\/\/.*$/;

    return innhold.replace(uriRegex, (match) => {
        const matched = match.match(httpRegex) ? match : `http://${match}`;
        return `<a target="_blank" href="${matched}">${matched}</a>`;
    });
};

export const tilAvsnitt = (avsnitt, index) => (
    <span dangerouslySetInnerHTML={{ __html: sanitize(avsnitt, { allowedTags: ['a'] }) }} key={index} />
);

export const prettyDate = (date) => moment(date).format('Do MMMM YYYY, [kl.] HH:mm');

export const shortDate = (date) => moment(date).format('DD.MM.YY');

export const getCookie = (name) => {
    const re = new RegExp(`${name}=([^;]+)`);
    const match = re.exec(document.cookie);
    return match !== null ? match[1] : '';
};

export const addXsrfHeader = (xhr) => {
    xhr.setRequestHeader('X-XSRF-TOKEN', getCookie('XSRF-TOKEN-MININNBOKS'));
};

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
