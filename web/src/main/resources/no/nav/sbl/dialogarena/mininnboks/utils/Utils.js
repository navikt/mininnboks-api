var React = require('react');
var sanitize = require('sanitize-html');

var Utils = {
    sanitize: function (tekst) {
        return sanitize(tekst);
    },
    leggTilLenkerTags: function (innhold) {
        var uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
        return innhold.replace(uriRegex, '<a target="_blank" href="$1">$1</a>');
    },
    tilParagraf: function (kanInneholdeHTML) {
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
    voidTransformation: function (x) {
        return x;
    },
    whenFinished: function (promiselist) {
        var d = $.Deferred();
        var result = [];
        var resolved = 0;
        var isRejected = false;

        promiselist.forEach(function (promise, index) {
            promise.always(function (data) {
                result[index] = data;
                resolved++;
            }).fail(function () {
                isRejected = true;
                resolve();
            }).done(function () {
                resolve();
            });
        });

        function resolve() {
            if (resolved < promiselist.length) {
                return;
            }
            if (isRejected) {
                d.reject.apply(d, result);
            } else {
                d.resolve.apply(d, result);
            }
        }

        return d.promise();
    }
};

module.exports = Utils;