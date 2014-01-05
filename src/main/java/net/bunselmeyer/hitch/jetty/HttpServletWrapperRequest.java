package net.bunselmeyer.hitch.jetty;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;
import net.bunselmeyer.hitch.app.AbstractRequest;
import net.bunselmeyer.hitch.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpServletWrapperRequest extends AbstractRequest {

    private final HttpServletRequest httpRequest;

    public HttpServletWrapperRequest(HttpServletRequest httpRequest) {
        super(httpRequest.getRequestURI());
        this.httpRequest = httpRequest;
        this.cookies.putAll(buildCookies(httpRequest));
        this.headers.putAll(buildHeaders(httpRequest));
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
