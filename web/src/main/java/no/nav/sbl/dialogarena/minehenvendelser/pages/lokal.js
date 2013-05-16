$(function () {
    $('.kvittering-link-right').each(function () {
        var $element, $link  , $section;
        $element = $(this);
        $link = $element.find('a');
        $section = $element.parent().find('.kvittering');
        $link.on('click', function (event) {
            if ($link.text() === "Vis kvittering") {
                $link.text('Skjul kvittering');
                $section.animate({
                    height: 'toggle'
                }, 500);
            } else {
                $link.text('Vis kvittering');
                $section.animate({
                    height: 'toggle'
                }, 500);
            }
            event.preventDefault();
        });
    });
    $(".tooltiplink").tooltip();
});