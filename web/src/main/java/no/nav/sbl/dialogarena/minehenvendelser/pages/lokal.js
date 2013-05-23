$(function () {
    $('.kvittering-link-right').each(function () {
        var $element, $linkVis, $linkSkjul, $kvittering;
        $element = $(this);
        $linkVis = $element.find('#vis');
        $linkSkjul = $element.find('#skjul');
        $kvittering = $element.parent().find('.kvittering');
        $linkVis.on('click', toggleKvittering($linkSkjul, $linkVis, $kvittering));
        $linkSkjul.on('click', toggleKvittering($linkVis, $linkSkjul, $kvittering));
    });

    function toggleKvittering($linkVis, $linkSkjul, $kvittering) {
        return function (event) {
            $linkVis.css("display", "inline");
            $linkSkjul.css("display", "none");
            $kvittering.animate({
                height: 'toggle'
            }, 500);
            event.preventDefault();
        };
    }
    $(".tooltiplink").tooltip();
});