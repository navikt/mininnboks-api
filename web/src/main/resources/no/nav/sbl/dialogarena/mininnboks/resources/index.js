var cache = {};
var promise = undefined;

init();

function init() {
    promise = $.get('/mininnboks/tjenester/resources').done(function (result) {
        cache = result;
    });
}

module.exports = {
    promise: promise,
    get: function(key) {
        return cache[key];
    }
};