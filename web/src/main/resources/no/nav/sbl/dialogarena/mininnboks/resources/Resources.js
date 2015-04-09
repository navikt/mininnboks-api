var cache = {};

function get(key) {
    return cache[key] || '';
}

function fetch() {
    return $.get('/mininnboks/tjenester/resources').done(function (result) {
        $.extend(cache, result);
    });
}

function hasKey(key) {
    return cache.hasOwnProperty(key);
}

module.exports = {
    fetch: fetch,
    get: get,
    hasKey: hasKey
};