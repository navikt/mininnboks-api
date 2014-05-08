$(document).ready(function() {

    $('.flipp').on('click', function () {
        var $toggleable = $(this).parent('.toggleable');
        if ($toggleable.hasClass('closed')) {
            $toggleable.removeClass('closed');
        } else {
            $toggleable.addClass('closed');
        }
    });

});
