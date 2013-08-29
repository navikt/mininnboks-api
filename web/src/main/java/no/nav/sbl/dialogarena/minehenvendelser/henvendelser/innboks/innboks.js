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

    attachMeldingListener();
    attachTilInnboksListener();
});