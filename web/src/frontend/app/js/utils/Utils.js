import React from 'react';
import sanitize from 'sanitize-html';
import format from 'string-format';
import moment from 'moment';
import { formatHTMLMessage } from 'react-intl';
import 'moment/locale/nb';
import Constants from './Constants';

moment.locale('nb');

export const leggTilLenkerTags = (innhold) => {
    var uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
    var httpRegex = /^(https?):\/\/.*$/;

    return innhold.replace(uriRegex, function (match) {
        match = match.match(httpRegex) ? match : 'http://' + match;
        return '<a target="_blank" href="' + match + '">' + match + '</a>'
    });
};

export const tilAvsnitt = (avsnitt) => {
    return <span dangerouslySetInnerHTML={{__html: sanitize(avsnitt, {allowedTags: ['a']})}}></span>;
};

export const prettyDate = (date) => {
    return moment(date).format('Do MMMM YYYY, [kl.] HH:mm');
};

export const shortDate = (date) => {
    return moment(date).format('DD.MM.YY')
};

export const getCookie = (name) => {
    var re = new RegExp(name + '=([^;]+)');
    var match = re.exec(document.cookie);
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
    } else {
        return Constants.LEST;
    }
};


