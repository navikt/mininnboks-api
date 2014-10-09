// Keyboard shortcuts
$(document).on('keydown', function(e) {
    // Submit på ctrl + enter
    if(e.ctrlKey && e.keyCode == 13) {
        $(".send-link").click();
    }
});
