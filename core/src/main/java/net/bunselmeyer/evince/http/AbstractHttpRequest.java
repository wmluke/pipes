package net.bunselmeyer.evince.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class AbstractHttpRequest implements HttpRequest {

    protected final QueryStringDecoder queryStringDecoder;
    protected final Map<String, Cookie> cookies = new LinkedHashMap<>();
    protected final Map<String, String> headers = new LinkedHashMap<>();
    protected final Map<String, String> routeParams = new LinkedHashMap<>();

    public AbstractHttpRequest(String queryString) {
        this.queryStringDecoder = new QueryStringDecoder("?" + queryString);
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
        Map<String, List<String>> params = queryParams();
        return params.containsKey(name) ? params.get(name) : Collections.emptyList();
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
        return routeParams;
    }

    @Override
    public String routeParam(String name) {
        return routeParams.get(name);
    }
}
