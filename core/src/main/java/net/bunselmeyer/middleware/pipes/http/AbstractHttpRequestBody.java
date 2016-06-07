package net.bunselmeyer.middleware.pipes.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public abstract class AbstractHttpRequestBody implements HttpRequest.Body {

    private final ObjectMapper jsonMapper;
    private final ObjectMapper xmlMapper;

    private ByteArrayInputStream bufferedInputStream;

    public AbstractHttpRequestBody(ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        this.jsonMapper = jsonMapper;
        this.xmlMapper = xmlMapper;
    }

    // TODO: not sure i like this...feel like loosing all the efficiency gained by using an InputStream
    //       may want to consider adding a `readIS` flag and throwing an exception if the IS has 
    //       already been read or perhaps cache `asText`, `fromJson`, and `fromXml`
    private InputStream asBufferedInputStream() {
        try {
            if (bufferedInputStream == null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                IOUtils.copy(asInputStream(), buffer);
                bufferedInputStream = new ByteArrayInputStream(buffer.toByteArray());
            } else {
                bufferedInputStream.reset();
            }
            return bufferedInputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asText() {
        try {
            return IOUtils.toString(asBufferedInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, List<String>> asFormUrlEncoded() {
        return new QueryStringDecoder("?" + asText(), Charset.forName("UTF-8")).parameters();
    }

    @Override
    public JsonNode fromJson() {
        try {
            return jsonMapper.readTree(asBufferedInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B fromJson(Class<B> type) {
        try {
            return jsonMapper.readValue(asBufferedInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B fromJson(TypeReference<B> type) {
        try {
            return jsonMapper.readValue(asBufferedInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B fromXml(Class<B> type) {
        try {
            return xmlMapper.readValue(asBufferedInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <B> B fromXml(TypeReference type) {
        try {
            return xmlMapper.readValue(asBufferedInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
