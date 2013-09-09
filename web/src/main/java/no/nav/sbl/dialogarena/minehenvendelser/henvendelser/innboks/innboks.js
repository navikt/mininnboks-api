$(document).ready(function() {

    var distanseFraToppen = 0;

    var attachMeldingListener = function() {
        $('.melding').on('click', function() {
            distanseFraToppen = $('#meldinger').scrollTop();
            attachAjaxCompleteListener();
        });
    };

    var attachTilInnboksListener = function() {
        $('.tilbake-til-innboks-link').on('click', function() {
            attachAjaxCompleteListener();
        });
    };

    var attachAjaxCompleteListener = function() {
        $(document).one('ajaxComplete', function() {
            $('#meldinger').scrollTop(distanseFraToppen);
            attachMeldingListener();
            attachTilInnboksListener();
            attachToggleHoydeListener();
        });
    };

    var attachToggleHoydeListener = function() {
        var $tidligereHenvendelse = $('.tidligere-henvendelse p');
        var $utvideTekstPil = $('.utvide-tekst-pil');
        var minHoyde = 50;

        $tidligereHenvendelse.each(function() {
            $(this).data('height', $(this).height());
        });

        $tidligereHenvendelse.height(minHoyde);

        $utvideTekstPil.on('click', function() {
            var $tekstFelt = $(this).siblings('p');
            if($tekstFelt.height() == minHoyde) {
                $tekstFelt.animate({height: $tekstFelt.data('height')});
            } else {
                $tekstFelt.animate({height: minHoyde});
            }
            $(this).toggleClass('rotert');
        });
    };

    var adjustInnboksHeight = function() {
        var bodyHeight = $('body').outerHeight();
        var restHeight = $('.footer').outerHeight() + $('.innstillinger-innlogget').outerHeight() +
            $('.rad-logo').outerHeight() + $('#innboks-top').outerHeight();
        $('#innboks-container').height(bodyHeight - restHeight);
    };

    attachToggleHoydeListener();
    attachMeldingListener();
    attachTilInnboksListener();
    adjustInnboksHeight();
});