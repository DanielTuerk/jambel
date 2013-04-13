package com.jambit.jambel.server.mvc;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.joda.time.DateTime;

/**
 * Logback log appender to show current logs on the logging view.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class LoggingAppender extends AppenderBase<ILoggingEvent> {

    private final static StringBuilder stringBuilder = new StringBuilder();

    protected void append(ILoggingEvent event) {
        stringBuilder.append(new DateTime(event.getTimeStamp()).toString("yyyy-MM-dd HH:mm:ss"));
        stringBuilder.append(": ");
        stringBuilder.append(event.toString());
        stringBuilder.append("\n");
    }

    public static String getMessages() {
        return stringBuilder.toString();
    }
}
