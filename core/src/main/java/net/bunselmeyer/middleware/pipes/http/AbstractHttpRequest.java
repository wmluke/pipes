package net.bunselmeyer.middleware.pipes.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.bunselmeyer.middleware.util.OptionalString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;

public abstract class AbstractHttpRequest implements HttpRequest {

    protected final QueryStringDecoder queryStringDecoder;
    protected final Map<String, Cookie> cookies = new LinkedHashMap<>();
    protected final Map<String, String> headers = new LinkedHashMap<>();

    private final static String dateReceiveFmt[] =
        {
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd-MMM-yy HH:mm:ss",
            "EEE MMM dd HH:mm:ss yyyy",

            "EEE, dd MMM yyyy HH:mm:ss", "EEE dd MMM yyyy HH:mm:ss zzz",
            "EEE dd MMM yyyy HH:mm:ss", "EEE MMM dd yyyy HH:mm:ss zzz", "EEE MMM dd yyyy HH:mm:ss",
            "EEE MMM-dd-yyyy HH:mm:ss zzz", "EEE MMM-dd-yyyy HH:mm:ss", "dd MMM yyyy HH:mm:ss zzz",
            "dd MMM yyyy HH:mm:ss", "dd-MMM-yy HH:mm:ss zzz", "dd-MMM-yy HH:mm:ss", "MMM dd HH:mm:ss yyyy zzz",
            "MMM dd HH:mm:ss yyyy", "EEE MMM dd HH:mm:ss yyyy zzz",
            "EEE, MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yy HH:mm:ss zzz",
            "EEE dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss",
        };

    public AbstractHttpRequest(String uri) {
        this.queryStringDecoder = new QueryStringDecoder(StringUtils.trimToEmpty(uri));
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
    public Map<String, List<String>> queryParams() {
        return queryStringDecoder.parameters();
    }

    @Override
    public List<String> queryParams(String name) {
        Map<String, List<String>> params = queryParams();
        return params.containsKey(name) ? params.get(name) : Collections.emptyList();
    }

    @Override
    public OptionalString queryParam(String name) {
        Iterator<String> iterator = queryParams(name).iterator();
        return iterator.hasNext() ? optionalOf(iterator.next()) : OptionalString.empty();
    }

    @Override
    public Map<String, String> headers() {
        return this.headers;
    }

    @Override
    public OptionalString header(String name) {
        return optionalOf(headers().get(name));
    }

    @Override
    public OptionalString routeParam(String name) {
        return optionalOf(routeParams().get(name));
    }

    @Override
    public OptionalLong dateHeader(String name) {
        return header(name)
            .map((dateHeader) -> {
                try {
                    return OptionalLong.of(DateUtils.parseDate(dateHeader, dateReceiveFmt).getTime());
                } catch (ParseException e) {
                    return null;
                }
            })
            .orElse(OptionalLong.empty());
    }

    @Override
    public Map<String, Cookie> cookies() {
        return cookies;
    }

    @Override
    public Optional<Cookie> cookie(String name) {
        return Optional.ofNullable(cookies().get(name));
    }

    private static OptionalString optionalOf(String value) {
        return OptionalString.ofNullable(value);
    }
}
