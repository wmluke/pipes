package net.bunselmeyer.hitch.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import net.bunselmeyer.hitch.app.Request;
import net.bunselmeyer.hitch.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NettyWrapperRequest implements Request {

    private final DefaultFullHttpRequest httpRequest;
    private final QueryStringDecoder queryStringDecoder;
    private final Map<String, Cookie> cookies;
    private final Map<String, String> headers;

    public NettyWrapperRequest(DefaultFullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        this.queryStringDecoder = new QueryStringDecoder(httpRequest.getUri());
        this.cookies = buildCookies(httpRequest);
        this.headers = buildHeaders(httpRequest);
    }

    @Override
    public String protocol() {
        return httpRequest.getProtocolVersion().protocolName();
    }

    @Override
    public String host() {
        return headers.get(HttpHeaders.Names.HOST.toString());
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
    public String uri() {
        return httpRequest.getUri();
    }

    @Override
    public String method() {
        return httpRequest.getMethod().name();
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
    public Map<String, List<String>> queryParams() {
        return queryStringDecoder.parameters();
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
        String json = bodyAsText();
        if (json == null) {
            return null;
        }
        return JsonUtil.fromJson(json, type);
    }

    @Override
    public String bodyAsText() {
        ByteBuf content = httpRequest.content();
        if (!content.isReadable()) {
            return null;
        }
        return content.toString(Charset.forName("UTF-8"));
    }

//    public Map<String, String> bodyAsFormUrlEncoded() {
//        Map<String, String> data = new LinkedHashMap<>();
//
//        HttpPostStandardRequestDecoder decoder = new HttpPostStandardRequestDecoder(httpRequest);
//
//
//        for (InterfaceHttpData httpData : decoder.getBodyHttpDatas()) {
//            String name = httpData.getName();
//        }
//        return data;
//    }

    private Map<String, String> buildHeaders(HttpRequest httpRequest) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : httpRequest.headers()) {
            headers.put(entry.getKey(), entry.getValue());
        }
        return headers;
    }

    private Map<String, Cookie> buildCookies(HttpRequest httpRequest) {
        LinkedHashMap<String, Cookie> cookies = new LinkedHashMap<>();
        String cookieString = httpRequest.headers().get(HttpHeaders.Names.COOKIE);
        if (StringUtils.isNotBlank(cookieString)) {
            for (Cookie cookie : CookieDecoder.decode(cookieString)) {
                cookies.put(cookie.getName(), cookie);
            }
        }
        return cookies;
    }

}
