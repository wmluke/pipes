package net.bunselmeyer.hitch.app;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public abstract class AbstractRequest implements Request {

    protected final QueryStringDecoder queryStringDecoder;
    protected final Map<String, Cookie> cookies = new LinkedHashMap<>();
    protected final Map<String, String> headers = new LinkedHashMap<>();

    public AbstractRequest(String uri) {
        this.queryStringDecoder = new QueryStringDecoder(uri);
    }

    @Override
    public String path() {
        return queryStringDecoder.path();
    }

    @Override
    public String query() {
        String uri = uri();
        int i = uri.indexOf('?');
        if (i == -1) {
            return "";
        }
        return StringUtils.substring(uri, i);
    }

    @Override
    public Map<String, List<String>> queryParams() {
        return queryStringDecoder.parameters();
    }

    @Override
    public List<String> queryParams(String name) {
        return queryParams().get(name);
    }

    @Override
    public String queryParam(String name) {
        Iterator<String> iterator = queryParams(name).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public Map<String, String> headers() {
        return this.headers;
    }

    @Override
    public String header(String name) {
        return headers().get(name);
    }

    @Override
    public Map<String, Cookie> cookies() {
        return cookies;
    }

    @Override
    public Cookie cookie(String name) {
        return cookies().get(name);
    }

    @Override
    public Map<String, String> routeParams() {
        return null;
    }

    @Override
    public String routeParam(String name) {
        return null;
    }

    @Override
    public Map<String, List<String>> bodyPostParameters() {
        String postParams = null;
        try {
            postParams = bodyAsText();
        } catch (IOException e) {
            return new HashMap<>();
        }
        return new QueryStringDecoder(postParams).parameters();
    }

    @Override
    public List<String> bodyPostParameters(String name) {
        return bodyPostParameters().get(name);
    }

    @Override
    public String bodyPostParameter(String name) {
        Iterator<String> iterator = bodyPostParameters(name).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }
}
