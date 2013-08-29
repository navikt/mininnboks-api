$(document).ready(function() {

    var distanseFraToppen = 0;

    var attachMeldingListener = function() {
        $('.melding').on('click', function() {
            distanseFraToppen = $('#meldinger').scrollTop();
            attachAjaxCompleteListener();
        });
    }

    var attachTilInnboksListener = function() {
        $('.tilbake-til-innboks-link').on('click', function() {
          attachAjaxCompleteListener();
        });
    }

    var attachAjaxCompleteListener = function() {
        $(document).one('ajaxComplete', function() {
            $('#meldinger').scrollTop(distanseFraToppen);
            attachMeldingListener();
            attachTilInnboksListener();
        });
    }

    var adjustInnboksHeight = function() {
        var bodyHeight = $('body').outerHeight();
        var restHeight = $('.footer').outerHeight() + $('.innstillinger-innlogget').outerHeight() +
                         $('.rad-logo').outerHeight() + $('#innboks-top').outerHeight();
        $('#innboks-container').height(bodyHeight - restHeight);
    }

    attachMeldingListener();
    attachTilInnboksListener();
    adjustInnboksHeight();
    $(window).on('resize', function() { adjustInnboksHeight(); });
});