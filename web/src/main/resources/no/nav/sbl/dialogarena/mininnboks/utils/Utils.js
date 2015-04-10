var React = require('react/addons');
var sanitize = require('sanitize-html');
var format = require('string-format');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

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
    ariaLabelForMelding: function(antallMeldinger, melding){
        var behandlingsStatus = '';
        if (melding.type === 'SPORSMAL_MODIA_UTGAAENDE' || !melding.lest) {
            behandlingsStatus += 'Ubehandlet,';
        } else if (melding.type == 'SVAR_SBL_INNGAAENDE') {
            behandlingsStatus += 'Besvart,';
        }
        return format('{0} {1} {2}, {3}',
            behandlingsStatus,
            antallMeldinger,
            antallMeldinger === 1 ? 'melding' : 'meldinger'
        );
    }
};

module.exports = Utils;