package net.bunselmeyer.middleware.util;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.ServerCookieDecoder;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUtil {

    public static Map<String, Cookie> parseCookieHeader(String cookieHeader) {
        LinkedHashMap<String, Cookie> cookies = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(cookieHeader)) {
            for (Cookie cookie : ServerCookieDecoder.decode(cookieHeader)) {
                cookies.put(cookie.name(), cookie);
            }
        }
        return cookies;
    }
}
