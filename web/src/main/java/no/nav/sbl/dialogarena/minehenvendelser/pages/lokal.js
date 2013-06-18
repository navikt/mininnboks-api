$(function () {
    $('.kvittering').each(function () {
        var $linkVis, $linkSkjul, $kvittering;
        $kvittering = $(this);
        $linkVis = $kvittering.parent().find('#vis');
        $linkSkjul = $kvittering.parent().find('#skjul');
        $linkVis.on('click', toggleKvittering($linkSkjul, $linkVis, $kvittering));
        $linkSkjul.on('click', toggleKvittering($linkVis, $linkSkjul, $kvittering));
    });

    $('article h1[class*=robust]').each(function() {
        if ($(this).height() > 20) {
            $(this).removeClass('robust-strek').addClass('robust-lang-strek');
        }
    });

    $('.tooltiplink').tooltip();

    function toggleKvittering($linkVis, $linkSkjul, $kvittering) {
        return function (event) {
            $linkVis.removeAttr('hidden');
            $linkVis.attr('aria-hidden','false');
            $linkSkjul.attr('hidden', true);
            $linkSkjul.prop('aria-hidden','true');
            $kvittering
                .animate({height: 'toggle'}, 500)
                .css('display', 'inline-block');
            $kvittering.parent().children(':first').toggleClass('uten-ramme');
            event.preventDefault();
        };
    }
});