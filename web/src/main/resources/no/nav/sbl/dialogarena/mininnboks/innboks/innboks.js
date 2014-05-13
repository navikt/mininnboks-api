var Innboks = (function() {
    var markerSomLest = function(traadID) {
        $('#' + traadID).addClass('lest');
    };

    var toggleTraad = function(traadID) {
        var $traad = $('#' + traadID);
        if ($traad.hasClass('closed')) {
            $traad.removeClass('closed');
        } else {
            $traad.addClass('closed');
        }
    };

    return {
        markerSomLest : markerSomLest,
        toggleTraad : toggleTraad
    };
})();
