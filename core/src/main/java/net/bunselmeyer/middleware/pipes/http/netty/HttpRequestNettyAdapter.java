package net.bunselmeyer.middleware.pipes.http.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequest;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequestBody;
import net.bunselmeyer.middleware.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestNettyAdapter extends AbstractHttpRequest {

    private final DefaultFullHttpRequest httpRequest;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper xmlMapper;
    private final Map<String, String> routeParams = new LinkedHashMap<>();

    public HttpRequestNettyAdapter(DefaultFullHttpRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        super(httpRequest.uri());
        this.httpRequest = httpRequest;
        this.jsonMapper = jsonMapper;
        this.xmlMapper = xmlMapper;
        this.cookies.putAll(buildCookies(httpRequest));
        this.headers.putAll(buildHeaders(httpRequest));
    }

    @Override
    public String protocol() {
        return String.valueOf(httpRequest.protocolVersion().protocolName());
    }

    @Override
    public String host() {
        return headers().get(HttpHeaderNames.HOST.toString());
    }

    @Override
    public String uri() {
        return httpRequest.uri();
    }

    @Override
    public String method() {
        return String.valueOf(httpRequest.method().name());
    }

    @Override
    public Body body() {
        return new Body();
    }

    @Override
    public HttpServletRequest delegate() {
        return null;
    }

    @Override
    public Map<String, String> routeParams() {
        return routeParams;
    }

    @Override
    public String routeParam(String name) {
        return routeParams.get(name);
    }

    private Map<String, String> buildHeaders(HttpRequest httpRequest) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<CharSequence, CharSequence> entry : httpRequest.headers()) {
            headers.put((String) entry.getKey(), (String) entry.getValue());
        }
        return headers;
    }

    private Map<String, Cookie> buildCookies(HttpRequest httpRequest) {
        return HttpUtil.parseCookieHeader((String) httpRequest.headers().get(HttpHeaderNames.COOKIE));
    }

    private class Body extends AbstractHttpRequestBody {

        Body() {
            super(null, jsonMapper, xmlMapper);
        }

        @Override
        public InputStream asInputStream() {
            return new ByteBufInputStream(httpRequest.content());
        }
    }
}
