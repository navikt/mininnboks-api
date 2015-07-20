var cache = {};
var promise = $.Deferred().promise();


function get(key) {
    return cache[key] || '';
}

function fetch() {
    promise = $.get('/mininnboks/tjenester/resources')
        .done(function (result) {
            $.extend(cache, result);
        });
    return promise;
}

function hasKey(key) {
    return cache.hasOwnProperty(key);
}

function getPromise() {
    return promise;
}

module.exports = {
    fetch: fetch,
    get: get,
    hasKey: hasKey,
    getPromise: getPromise
};