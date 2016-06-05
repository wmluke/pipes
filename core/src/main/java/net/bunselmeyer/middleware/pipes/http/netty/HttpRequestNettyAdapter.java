package net.bunselmeyer.middleware.pipes.http.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
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

    public HttpRequestNettyAdapter(DefaultFullHttpRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        super(httpRequest.getUri());
        this.httpRequest = httpRequest;
        this.jsonMapper = jsonMapper;
        this.xmlMapper = xmlMapper;
        this.cookies.putAll(buildCookies(httpRequest));
        this.headers.putAll(buildHeaders(httpRequest));
    }

    @Override
    public String protocol() {
        return httpRequest.getProtocolVersion().protocolName();
    }

    @Override
    public String host() {
        return headers().get(HttpHeaders.Names.HOST.toString());
    }

    @Override
    public String uri() {
        return httpRequest.getUri();
    }

    @Override
    public String method() {
        return httpRequest.getMethod().name();
    }

    @Override
    public Body body() {
        return new Body();
    }

    @Override
    public HttpServletRequest delegate() {
        return null;
    }

    private Map<String, String> buildHeaders(HttpRequest httpRequest) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : httpRequest.headers()) {
            headers.put(entry.getKey(), entry.getValue());
        }
        return headers;
    }

    private Map<String, Cookie> buildCookies(HttpRequest httpRequest) {
        return HttpUtil.parseCookieHeader(httpRequest.headers().get(HttpHeaders.Names.COOKIE));
    }

    protected class Body extends AbstractHttpRequestBody {

        protected Body() {
            super(null, jsonMapper, xmlMapper);
        }

        @Override
        public InputStream asInputStream() {
            return new ByteBufInputStream(httpRequest.content());
        }
    }
}
