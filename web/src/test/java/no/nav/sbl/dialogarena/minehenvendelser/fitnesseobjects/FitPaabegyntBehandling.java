package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FitPaabegyntBehandling {

    /* Plukker typisk ut 'Sist endret' og dato */
    public static final Pattern PATTERN = Pattern.compile("\\u2022.*([A-Z].*) (\\d?\\d\\. .* \\d\\d:\\d\\d)");
    public String navnPaaBehandling;
    public String datoOgTidspunkt;
    public String tekst;

    public FitPaabegyntBehandling() {
    }

    public FitPaabegyntBehandling(String tittel, String tekst) {
        this.navnPaaBehandling = tittel;

        Matcher matcher = PATTERN.matcher(tekst);
        matcher.find();
        this.tekst =  tekst.substring(matcher.start(1), matcher.end(1));
        this.datoOgTidspunkt = tekst.substring(matcher.start(2), matcher.end(2));
    }
}
