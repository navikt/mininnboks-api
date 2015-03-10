var cache = {};
init();

function init(){
    $.get('/mininnboks/tjenester/resources').done(function(result){
        cache = result;
    });
}

module.exports = {
    get: function(key){
        return cache[key];
    }
};