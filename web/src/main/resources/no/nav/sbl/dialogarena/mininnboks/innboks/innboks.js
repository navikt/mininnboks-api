var Innboks = (function () {
    var markerSomLest = function (traadID) {
        $('#' + traadID).addClass('lest');
    };

    var toggleTraad = function (traadID) {
        var $traad = $('#' + traadID);
        if ($traad.hasClass('closed')) {
            $traad.removeClass('closed');
        } else {
            $traad.addClass('closed');
        }
    };
    $(document).ready(function () {
        $('#innboks-container').on('keydown', '.flipp', function (event) {
            if (event.keyCode === 32) {
                var $flipp = $(event.currentTarget);
                var $traad = $flipp.closest('.traad');
                toggleTraad($traad.attr('id'));
            }
        });
    });

    return {
        markerSomLest: markerSomLest,
        toggleTraad: toggleTraad
    };
})();
