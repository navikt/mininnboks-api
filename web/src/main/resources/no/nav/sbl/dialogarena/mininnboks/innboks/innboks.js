$(document).ready(function () {
    $('#innboks-container').on('keydown', '.flipp', function (event) {
        if (event.keyCode === 32) {
            var $flipp = $(event.currentTarget);
            $flipp.click();
            event.preventDefault();
        }
    });
});