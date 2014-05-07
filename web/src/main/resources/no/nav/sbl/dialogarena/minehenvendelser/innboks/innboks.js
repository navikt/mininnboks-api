$(document).ready(function() {

    $('.toggleable').on('click', function () {
        if ($(this).hasClass('closed')) {
            $(this).removeClass('closed');
        } else {
            $(this).addClass('closed');
        }
    });

});
