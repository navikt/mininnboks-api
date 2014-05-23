// Keyboard shortcuts
$(document).on('keydown', function(e) {
    // Submit på ctrl + enter
    if(e.ctrlKey && e.keyCode == 13) {
        $(".send-link").click();
    }
});

$(document).on('click', '.send-panel .pil-ned, .send-panel .tema-overskrift', function () {
    $('.endre-tema-wrapper').show();
    var handler = function () {
        $('.endre-tema-wrapper').hide();
        $(':not(.endre-tema)').off('click', handler)
    };

    $(':not(.endre-tema)').on('click', handler)
});