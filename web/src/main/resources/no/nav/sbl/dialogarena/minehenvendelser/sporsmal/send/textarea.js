var MAKS_TEGN = 1000;
var TEGN_IGJEN = " tegn igjen";
var TEGN_IGJEN_OVERSKRIDER = " tegn";
var OVERSKRIDER = "overskrider";

$(document).ready(function() {
    // Keyboard shortcuts
    $(document).on('keydown', function(e) {
        // Submit på ctrl + enter
        if(e.ctrlKey && e.keyCode == 13) {
            $(".send-link").click();
        }
    });

    var $tekst = $("#tekst");

    tellAntallTegn($tekst.get(0));

    $tekst.on('input keydown', function () {
        tilpassHoyde(this);
        tellAntallTegn(this);
    });
    $tekst.focus();

    function tilpassHoyde(textArea) {
        textArea.style.height = 'auto';
        textArea.style.height = Math.max(textArea.scrollHeight, 140) + 'px';
    }
    function tellAntallTegn(textArea) {
        var $antallTegn = $("#antall-tegn");
        var $antallTegnTekst = $("#antall-tegn-tekst");
        var antall = textArea.value.length;

        $antallTegn.text(MAKS_TEGN - antall);
        if (antall > MAKS_TEGN) {
            $antallTegn.addClass(OVERSKRIDER);
            $antallTegnTekst.text(TEGN_IGJEN_OVERSKRIDER);
        } else {
            $antallTegn.removeClass(OVERSKRIDER)
            $antallTegnTekst.text(TEGN_IGJEN);
        }
    }
});