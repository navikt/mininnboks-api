$(function () {
    $('.kvittering').each(function () {
        var $linkVis, $linkSkjul, $kvittering;
        $kvittering = $(this);
        $linkVis = $kvittering.parent().find('#vis');
        $linkSkjul = $kvittering.parent().find('#skjul');
        $linkVis.on('click', toggleKvittering($linkSkjul, $linkVis, $kvittering));
        $linkSkjul.on('click', toggleKvittering($linkVis, $linkSkjul, $kvittering));
    });

    $('h2[class*=robust]').each(function() {
        if ($(this).height() > 17) {
            $(this).addClass('overskrift-lang');
        }
    });

    $('.tooltiplink').tooltip();

    function toggleKvittering($linkVis, $linkSkjul, $kvittering) {
        return function (event) {
            $linkVis.css('display', 'inline');
            $linkSkjul.css('display', 'none');
            $kvittering
                .animate({height: 'toggle'}, 500)
                .css('display', 'inline-block');
            $kvittering.parent().children(':first').toggleClass('uten-ramme');
            event.preventDefault();
        };
    }
});