package net.bunselmeyer.hitch.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import net.bunselmeyer.hitch.app.AbstractRequest;
import net.bunselmeyer.hitch.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class NettyWrapperRequest extends AbstractRequest {

    private final DefaultFullHttpRequest httpRequest;

    public NettyWrapperRequest(DefaultFullHttpRequest httpRequest) {
        super(httpRequest.getUri());
        this.httpRequest = httpRequest;
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
