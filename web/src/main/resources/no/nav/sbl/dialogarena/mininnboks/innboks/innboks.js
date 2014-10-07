var Innboks = (function () {
    $(document).ready(function () {
        $('#innboks-container').on('keydown', '.flipp', function (event) {
            if (event.keyCode === 32) {
                var $flipp = $(event.currentTarget);
                var $traad = $flipp.closest('.traad');
                toggleTraad($traad.attr('id'));
            }
        });
    });
})();
