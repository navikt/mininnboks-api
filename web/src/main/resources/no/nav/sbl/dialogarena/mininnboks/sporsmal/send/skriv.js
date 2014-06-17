// Keyboard shortcuts
$(document).on('keydown', function(e) {
    // Submit på ctrl + enter
    if(e.ctrlKey && e.keyCode == 13) {
        $(".send-link").click();
    }
});

$(document).on('click', function () {
    $('.endre-tema-wrapper').hide();
});

$(document).on('click', '.send-panel .pil-ned, .send-panel .tema-overskrift', function (e) {
    $('.endre-tema-wrapper').toggle();
    e.stopPropagation();
});

$(document).on('click', '.endre-tema', function (e) {
    e.stopPropagation();
});
