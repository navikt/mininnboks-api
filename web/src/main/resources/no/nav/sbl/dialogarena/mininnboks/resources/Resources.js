var cache = {};

function get(key) {
    return cache[key] || '';
}

function fetch() {
    return $.get('/mininnboks/tjenester/resources').done(function (result) {
        $.extend(cache, result);
    });
}

module.exports = {
    fetch: fetch,
    get: get
};