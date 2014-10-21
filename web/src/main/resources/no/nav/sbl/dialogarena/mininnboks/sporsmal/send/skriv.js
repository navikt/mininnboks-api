// Keyboard shortcuts
$(document).on('keydown', function(e) {
    // Submit på ctrl + enter
    if(e.ctrlKey && e.keyCode == 13) {
        $(".send-link").click();
    }
});

$(document).on('click', function () {
    $('.endre-temagruppe-wrapper').hide();
});

$(document).on('click', '.send-panel .pil-ned, .send-panel .temagruppe-overskrift', function (e) {
    $('.endre-temagruppe-wrapper').toggle();
    e.stopPropagation();
});

$(document).on('click', '.endre-temagruppe', function (e) {
    e.stopPropagation();
});
