package net.bunselmeyer.middleware.pipes.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class AbstractHttpRequestBody implements HttpRequest.Body {

    private static final String BODY_TEXT = "BODY_TEXT";

    private final HttpServletRequest httpRequest;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper xmlMapper;

    protected AbstractHttpRequestBody(HttpServletRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        this.httpRequest = httpRequest;
        this.jsonMapper = jsonMapper;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public String asText() {
        try {
            if (httpRequest.getAttribute(BODY_TEXT) == null) {
                httpRequest.setAttribute(BODY_TEXT, IOUtils.toString(asInputStream(), "UTF-8"));
            }
            return (String) httpRequest.getAttribute(BODY_TEXT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, List<String>> asFormUrlEncoded() {
        return new QueryStringDecoder("?" + asText()).parameters();
    }

    @Override
    public JsonNode asJson() {
        try {
            return jsonMapper.readTree(asText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B asJson(Class<B> type) {
        try {
            return jsonMapper.readValue(asText(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B asJson(TypeReference<B> type) {
        try {
            return jsonMapper.readValue(asText(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B asXml(Class<B> type) {
        try {
            return xmlMapper.readValue(asText(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B asXml(TypeReference type) {
        try {
            return xmlMapper.readValue(asText(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
