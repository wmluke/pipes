package net.bunselmeyer.hitch.jetty;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;
import net.bunselmeyer.hitch.app.Request;
import net.bunselmeyer.hitch.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class HttpServletWrapperRequest implements Request {

    private final HttpServletRequest httpRequest;
    private final Map<String, Cookie> cookies;
    private final Map<String, String> headers;

    public HttpServletWrapperRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
        this.headers = buildHeaders(httpRequest);
        this.cookies = buildCookies(httpRequest);
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
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public String header(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, Cookie> cookies() {
        return cookies;
    }

    @Override
    public Cookie cookie(String name) {
        return cookies.get(name);
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
    public Map<String, List<String>> queryParams() {
        ListMultimap<String, String> params = ArrayListMultimap.create();
        Enumeration<String> names = httpRequest.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            for (String value : httpRequest.getParameterValues(name)) {
                params.put(name, value);
            }
        }
        return Multimaps.asMap(params);
    }

    @Override
    public List<String> queryParam(String name) {
        return queryParams().get(name);
    }

    @Override
    public String queryFirstParam(String name) {
        Iterator<String> iterator = queryParam(name).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public <B> B bodyAsJson(Class<B> type) throws IOException {
        return JsonUtil.fromJson(httpRequest.getInputStream(), type);
    }

    @Override
    public String bodyAsText() throws IOException {
        return IOUtils.toString(httpRequest.getInputStream());
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
}
