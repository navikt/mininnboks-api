var React = require('react/addons');
var sanitize = require('sanitize-html');
var format = require('string-format');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');
var Constants = require('./Constants');

var Utils = {
    sanitize: function (tekst) {
        return sanitize(tekst);
    },
    leggTilLenkerTags: function (innhold) {
        var uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
        return innhold.replace(uriRegex, '<a target="_blank" href="$1">$1</a>');
    },
    tilAvsnitt: function (kanInneholdeHTML) {
        if (kanInneholdeHTML) {
            return function (avsnitt) {
                avsnitt = sanitize(avsnitt, {allowedTags: ['a']});
                return <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>;
            }
        } else {
            return function (avsnitt) {
                return <p>{avsnitt}</p>;
            }
        }
    },
    prettyDate: function (date) {
        return moment(date).format('Do MMMM YYYY, [kl.] HH:mm');
    },
    getCookie: function (name) {
        var re = new RegExp(name + '=([^;]+)');
        var match = re.exec(document.cookie);
        return match !== null ? match[1] : '';
    },
    addXsrfHeader: function (xhr) {
        xhr.setRequestHeader('X-XSRF-TOKEN', Utils.getCookie('XSRF-TOKEN-MININNBOKS'));
    },
    status: function (melding) {
        if (melding.type === 'SVAR_SBL_INNGAAENDE') {
            return Constants.BESVART;
        } else if (!melding.lest) {
            return Constants.IKKE_LEST;
        } else if (melding.type === 'SPORSMAL_MODIA_UTGAAENDE') {
            return Constants.LEST_UBESVART;
        } else {
            return Constants.LEST;
        }
    }
};

module.exports = Utils;