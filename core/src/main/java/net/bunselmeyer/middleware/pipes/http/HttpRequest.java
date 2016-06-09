package net.bunselmeyer.middleware.pipes.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.middleware.util.OptionalString;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

public interface HttpRequest {

    String protocol();

    String host();

    String path();

    String query();

    String uri();

    String method();

    Map<String, String> headers();

    OptionalString header(String name);

    OptionalLong dateHeader(String name);

    Map<String, Cookie> cookies();

    Optional<Cookie> cookie(String name);

    Map<String, String> routeParams();

    OptionalString routeParam(String name);

    Map<String, List<String>> queryParams();

    List<String> queryParams(String name);

    OptionalString queryParam(String name);

    Body body();

    Optional<HttpSession> session(boolean create);

    Optional<HttpSession> session();

    HttpServletRequest delegate();

    interface Body {

        InputStream asInputStream();

        String asText();

        Map<String, List<String>> asFormUrlEncoded();

        JsonNode fromJson();

        <B> B fromJson(Class<B> type);

        <B> B fromJson(TypeReference<B> type);

        <B> B fromXml(Class<B> type);

        <B> B fromXml(TypeReference type);
    }
}
