package net.bunselmeyer.hitch;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.LoggerFactory;

public class AppConfiguration implements App.Configuration {

    private final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private final ObjectMapper jsonObjectMapper = new ObjectMapper();
    private final ObjectMapper xmlObjectMapper = new XmlMapper();

    @Override
    public ObjectMapper jsonObjectMapper() {
        return jsonObjectMapper;
    }

    @Override
    public ObjectMapper xmlObjectMapper() {
        return xmlObjectMapper;
    }

    @Override
    public LoggerContext loggerContext() {
        return loggerContext;
    }
}
