package net.bunselmeyer.hitch.container.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;
import net.bunselmeyer.hitch.http.AbstractHttpRequest;
import net.bunselmeyer.hitch.http.AbstractHttpRequestBody;
import net.bunselmeyer.hitch.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestServletAdapter extends AbstractHttpRequest {

    private final HttpServletRequest httpRequest;
    private final HttpRequest.Body body;

    public HttpRequestServletAdapter(HttpServletRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        super(httpRequest.getQueryString());
        this.httpRequest = httpRequest;
        this.body = new Body(httpRequest, jsonMapper, xmlMapper);
        this.headers.putAll(buildHeaders(httpRequest));
        this.cookies.putAll(buildCookies(httpRequest));
    }

    @Override
    public String protocol() {
        return httpRequest.getProtocol();
    }

    @Override
    public String host() {
        return httpRequest.getRemoteHost();
    }

    @Override
    public String path() {
        return httpRequest.getPathInfo();
    }

    @Override
    public String query() {
        return httpRequest.getQueryString();
    }

    @Override
    public String uri() {
        return httpRequest.getRequestURI();
    }

    @Override
    public String method() {
        return httpRequest.getMethod();
    }

    @Override
    public HttpRequest.Body body() {
        return body;
    }

    @Override
    public HttpServletRequest delegate() {
        return httpRequest;
    }

    private Map<String, String> buildHeaders(HttpServletRequest httpRequest) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, httpRequest.getHeader(header));
        }
        return headers;
    }

    private Map<String, Cookie> buildCookies(HttpServletRequest httpRequest) {
        LinkedHashMap<String, Cookie> cookies = new LinkedHashMap<>();
        String cookieString = httpRequest.getHeader(HttpHeaders.Names.COOKIE.toString());
        if (StringUtils.isNotBlank(cookieString)) {
            for (Cookie cookie : CookieDecoder.decode(cookieString)) {
                cookies.put(cookie.getName(), cookie);
            }
        }
        return cookies;
    }

    public static class Body extends AbstractHttpRequestBody {

        private final HttpServletRequest httpRequest;

        protected Body(HttpServletRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
            super(jsonMapper, xmlMapper);
            this.httpRequest = httpRequest;
        }

        @Override
        public InputStream asInputStream() {
            try {
                return httpRequest.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
