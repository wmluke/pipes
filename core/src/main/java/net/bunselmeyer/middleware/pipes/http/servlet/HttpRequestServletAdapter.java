package net.bunselmeyer.middleware.pipes.http.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.ServerCookieDecoder;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestServletAdapter extends AbstractHttpRequest {

    public static final String PATH_PARAMS = "pathParams";

    private final HttpServletRequest httpRequest;
    private final HttpRequest.Body body;

    private static String uri(HttpServletRequest httpRequest) {
        StringBuilder builder = new StringBuilder();
        builder.append(httpRequest.getPathInfo());
        if (StringUtils.isNotBlank(httpRequest.getQueryString())) {
            builder.append("?");
            builder.append(httpRequest.getQueryString());
        }
        return builder.toString();
    }

    public HttpRequestServletAdapter(HttpServletRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        super(uri(httpRequest));
        this.httpRequest = httpRequest;
        this.body = new HttpRequestServletBody(httpRequest, jsonMapper, xmlMapper);
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

    @Override
    public String routeParam(String name) {
        return routeParams().get(name);
    }

    @Override
    public long dateHeader(String name) {
        return httpRequest.getDateHeader(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> routeParams() {
        if (httpRequest.getAttribute(PATH_PARAMS) == null) {
            httpRequest.setAttribute(PATH_PARAMS, new HashMap<String, String>());
        }
        return (Map<String, String>) httpRequest.getAttribute(PATH_PARAMS);
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
        String cookieString = httpRequest.getHeader(HttpHeaders.COOKIE);
        if (StringUtils.isNotBlank(cookieString)) {
            for (Cookie cookie : ServerCookieDecoder.decode(cookieString)) {
                cookies.put(cookie.name(), cookie);
            }
        }
        return cookies;
    }
}
