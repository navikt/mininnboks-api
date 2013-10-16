package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.common;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Takes a date without any time information ({@link org.joda.time.LocalDate}) and
 * adds a time to make a {@link org.joda.time.DateTime}.
 */
public final class LeggPaaTidspunkt implements Transformer<LocalDate, DateTime> {

    public static final LeggPaaTidspunkt SLUTTEN_AV_DAGEN = new LeggPaaTidspunkt(new LocalTime(23, 59, 59));
    public static final LeggPaaTidspunkt STARTEN_PAA_DAGEN = new LeggPaaTidspunkt(new LocalTime(0, 0, 0));

    private LocalTime time;
    private DateTimeZone timeZone;

    public LeggPaaTidspunkt(LocalTime time) {
        this(time, DateTimeZone.getDefault());
    }

    public LeggPaaTidspunkt(LocalTime time, DateTimeZone timeZone) {
        this.time = time;
        this.timeZone = timeZone;
    }

    @Override
    public DateTime transform(LocalDate date) {
        return date.toDateTime(time, timeZone);
    }

}
