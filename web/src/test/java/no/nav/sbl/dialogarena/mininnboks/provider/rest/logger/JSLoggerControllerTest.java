package no.nav.sbl.dialogarena.mininnboks.provider.rest.logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JSLoggerControllerTest {

    private JSLoggerController controller = new JSLoggerController();
    private Logger logger = mock(Logger.class);
    {
        controller.logger = logger;
    }

    @Test
    public void loggerInfoNaarLoglevelErINFO() {
        LogLinje logLinje = new LogLinje();
        logLinje.level = "INFO";

        controller.log(logLinje);

        verify(logger, times(1)).info(anyString());
    }

    @Test
    public void loggerWarnNaarLoglevelErWARN() {
        LogLinje logLinje = new LogLinje();
        logLinje.level = "WARN";

        controller.log(logLinje);

        verify(logger, times(1)).warn(anyString());
    }

    @Test
    public void loggerErrorNaarLoglevelErERROR() {
        LogLinje logLinje = new LogLinje();
        logLinje.level = "ERROR";

        controller.log(logLinje);

        verify(logger, times(1)).error(anyString());
    }

    @Test
    public void loggerErrorNaarLoglevelIkkeErDefinert() {
        LogLinje logLinje = new LogLinje();
        logLinje.level = "IKKE_DEFINERT";

        controller.log(logLinje);

        verify(logger, times(1)).error(anyString(), anyString());
    }
}
