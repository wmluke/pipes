package net.bunselmeyer.hitch.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractHttpRequestBody implements HttpRequest.Body {

    private final ObjectMapper objectMapper;
    private Object transformedBody;

    protected AbstractHttpRequestBody(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public String asText() {
        try {
            return IOUtils.toString(asInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, List<String>> asFormUrlEncoded() {
        return new QueryStringDecoder(asText()).parameters();
    }

    @Override
    public JsonNode asJson() {
        try {
            return objectMapper.readTree(asInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B asJson(Class<B> type) {
        try {
            return objectMapper.readValue(asInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B asJson(TypeReference type) {
        try {
            return objectMapper.readValue(asInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> B asTransformed() {
        return (B) transformedBody;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> void transform(Supplier<B> transformer) {
        transformedBody = transformer.get();
    }
}
