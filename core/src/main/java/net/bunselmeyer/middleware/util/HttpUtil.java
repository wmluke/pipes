package net.bunselmeyer.middleware.util;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUtil {

    public static Map<String, Cookie> parseCookieHeader(String cookieHeader) {
        LinkedHashMap<String, Cookie> cookies = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(cookieHeader)) {
            for (Cookie cookie : CookieDecoder.decode(cookieHeader)) {
                cookies.put(cookie.getName(), cookie);
            }
        }
        return cookies;
    }
}
