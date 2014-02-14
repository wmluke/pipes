package app.configure;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;

import java.nio.charset.Charset;

public class Logback {

    public static void configure(LoggerContext loggerContext) {
        loggerContext.reset();

        StatusManager sm = loggerContext.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default Hitch configuration.", loggerContext));
        }

        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(loggerContext);
        ca.setWithJansi(true);
        ca.setName("console");

        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setCharset(Charset.forName("UTF-8"));
        pl.setContext(loggerContext);
        //pl.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl.setPattern("%msg%n");
        pl.start();

        ca.setEncoder(pl);
        ca.start();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(ca);
    }
}
