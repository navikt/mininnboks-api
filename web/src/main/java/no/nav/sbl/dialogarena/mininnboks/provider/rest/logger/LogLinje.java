package no.nav.sbl.dialogarena.mininnboks.provider.rest.logger;

public class LogLinje {
    public String level;
    public String message;
    public String url;
    public String jsFileUrl;
    public int lineNumber;

    @Override
    public String toString() {
        return message + " [url='"+url+"', jsFile='"+jsFileUrl+"', line='"+lineNumber+"']";
    }
}
