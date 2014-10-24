// Keyboard shortcuts
$(document).on('keydown', function(e) {
    // Submit på ctrl + enter
    if(e.ctrlKey && e.keyCode == 13) {
        $(".send-link").click();
    }
});
$(document).ready(function(){
    $('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});
    $('.betingelser').dialog({autoOpen: false, width: 700, modal: true, resizable: false});

    $('a[class^="svar"]').click(function(e) {
        var $cb = $('.betingelsevalgpanel input[type=checkbox]:first');
        if ($(e.target).is('.svar-godta') && !$cb.is(':checked')) {
            $cb.prop("checked", !$cb.prop("checked"));
        }else if ($(e.target).is('.svar-avbryt') && $cb.is(':checked')){
            $cb.prop("checked", !$cb.prop("checked"));
        }
        $('.betingelser').dialog('close');
    });

    $('body').animate({scrollTop: $('.siteheader').outerHeight()}, 250);

    //Skalering av temagruppe dropdown pga jQuery kopierer computed style.
    function oppdaterTemavelgerStorrelse(){
        $('.temagruppevelger').selectmenu("refresh");
    }
    $(window).resize(oppdaterTemavelgerStorrelse);
    $(window).on('orientationchange', oppdaterTemavelgerStorrelse);
});
