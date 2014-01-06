package net.bunselmeyer.hitch.app;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.bunselmeyer.hitch.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRequest implements Request {

    protected final QueryStringDecoder queryStringDecoder;
    protected final Map<String, Cookie> cookies = new LinkedHashMap<>();
    protected final Map<String, String> headers = new LinkedHashMap<>();
    protected final Map<String, String> routeParams = new LinkedHashMap<>();

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
        return routeParams;
    }

    @Override
    public String routeParam(String name) {
        return routeParams.get(name);
    }

    @Override
    public <B> B bodyAsJson(Class<B> type) {
        try {
            return JsonUtil.fromJson(bodyAsInputStream(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String bodyAsText() {
        try {
            return IOUtils.toString(bodyAsInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, List<String>> bodyPostParameters() {
        return new QueryStringDecoder(bodyAsText()).parameters();
    }

    @Override
    public List<String> bodyPostParameters(String name) {
        return bodyPostParameters().get(name);
    }

    @Override
    public String bodyPostParameter(String name) {
        List<String> values = bodyPostParameters(name);
        if (values == null) {
            return null;
        }
        Iterator<String> iterator = values.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }
}
