var verticalTabletWindowWidth = 768;

$(document).ready(function() {
    var harToKolonner = $(window).width() >= verticalTabletWindowWidth;

    var respondToWindowSize = function() {
        if ($(window).width() <= verticalTabletWindowWidth && harToKolonner) {
            $('#meldinger').width('100%');
            harToKolonner = false;
        } else if ($(window).width() > verticalTabletWindowWidth && !harToKolonner) {
            $('#meldinger').width('40%');
            harToKolonner = true;
        }
    };

    respondToWindowSize();

    $(window).resize(function() {
        respondToWindowSize();
    });

});

$(document).bind('ajaxComplete', function() {
    if ($(window).width() <= verticalTabletWindowWidth) {
        $('#meldinger').width('100%');
    } else {
        $('#meldinger').width('40%');
    }
});