
$(document).ready(function() {

    var verticalTabletWindowWidth = 768;
    var horizontalMobileWindowWidth = 480;

    var respondToWindowSize = function() {
        if ($(window).width() <= verticalTabletWindowWidth) {
            $('#detaljpanel').hide();
            $('#meldinger').width('100%');
        } else {
            $('#meldinger').width('40%');
            $('#detaljpanel').show();
        }
    }

    respondToWindowSize();

    $(window).resize(function() {
        respondToWindowSize();
    });
});