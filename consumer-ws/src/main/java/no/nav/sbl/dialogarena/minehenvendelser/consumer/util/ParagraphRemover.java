package no.nav.sbl.dialogarena.minehenvendelser.consumer.util;

public class ParagraphRemover {

    public static String remove(String input) {
        String text = input;
        if (text.startsWith("<p>")) {
            text = text.substring(3);
        }
        if (text.endsWith("</p>")) {
            text = text.substring(0, text.length() - 4);
        }
        return text;
    }

}
