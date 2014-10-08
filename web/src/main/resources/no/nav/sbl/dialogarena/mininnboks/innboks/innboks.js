var Innboks = (function () {

    function markerSomLest(traadID) {
        $('#' + traadID).addClass('lest');
    }

    function toggleTraad(traadID) {
        $toggleTraad($('#' + traadID));
    }

    function $toggleTraad($traad) {
        $traad.toggleClass('closed');
        $traad.find('[aria-expanded]').attr('aria-expanded', function(ind, attr){
            return attr !== 'true';
        });
    }

    function oppdaterTraadStatus(traadID, nyStatus, ariaStatus) {
        var $traad = $('#' + traadID);
        $traad.find('.status-tekst').text(nyStatus);
        $traad.find('ariahelper').text(ariaStatus);
    }
    $(document).ready(function () {
        $('innboks-container').on('keydown', '.flipp', function (event) {
            if (event.keyCode === 32) {
                var $flipp = $(event.currentTarget);
                $flipp.click();
            }
        });
    });

    return {
        markerSomLest: markerSomLest,
        toggleTraad: toggleTraad,
        oppdaterTraadStatus: oppdaterTraadStatus
    };
})();
