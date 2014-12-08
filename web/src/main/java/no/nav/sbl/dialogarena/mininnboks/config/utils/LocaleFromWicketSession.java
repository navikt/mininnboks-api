package no.nav.sbl.dialogarena.mininnboks.config.utils;

import org.apache.commons.collections15.Factory;

import java.util.Locale;

import static org.apache.wicket.Session.get;

/**
 * Henter Locale som er satt på Wicket {@link org.apache.wicket.Session}.
 */
public final class LocaleFromWicketSession implements Factory<Locale> {

    public static final Factory<Locale> INSTANCE = new LocaleFromWicketSession();

    @Override
    public Locale create() {
        return get().getLocale();
    }

    private LocaleFromWicketSession() { }
}