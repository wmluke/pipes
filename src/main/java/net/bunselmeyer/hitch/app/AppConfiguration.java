package net.bunselmeyer.hitch.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class AppConfiguration implements App.Configuration {

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
}
