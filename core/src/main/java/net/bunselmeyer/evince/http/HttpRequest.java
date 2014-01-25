package net.bunselmeyer.evince.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.Cookie;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface HttpRequest {

    String protocol();

    String host();

    String path();

    String query();

    String uri();

    String method();

    Map<String, String> headers();

    String header(String name);

    Map<String, Cookie> cookies();

    Cookie cookie(String name);

    Map<String, String> routeParams();

    String routeParam(String name);

    Map<String, List<String>> queryParams();

    List<String> queryParams(String name);

    String queryParam(String name);

    Body body();

    HttpServletRequest delegate();

    public static interface Body {

        InputStream asInputStream();

        String asText();

        Map<String, List<String>> asFormUrlEncoded();

        JsonNode asJson();

        <B> B asJson(Class<B> type);

        <B> B asJson(TypeReference<B> type);

        <B> B asTransformed();

        <B> B asTransformed(Class<B> hint);

        <B> void transform(Supplier<B> transformer);

        <B> B asXml(Class<B> type);

        <B> B asXml(TypeReference type);
    }
}
