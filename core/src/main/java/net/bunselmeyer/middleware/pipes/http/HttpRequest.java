package net.bunselmeyer.middleware.pipes.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.Cookie;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface HttpRequest {

    String protocol();

    String host();

    String path();

    String query();

    String uri();

    String method();

    Map<String, String> headers();

    String header(String name);

    long dateHeader(String name);

    Map<String, Cookie> cookies();

    Cookie cookie(String name);

    Map<String, String> routeParams();

    String routeParam(String name);

    Map<String, List<String>> queryParams();

    List<String> queryParams(String name);

    String queryParam(String name);

    Body body();

    HttpSession session(boolean start);

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
