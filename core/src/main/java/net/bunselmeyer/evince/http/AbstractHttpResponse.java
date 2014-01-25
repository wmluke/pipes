package net.bunselmeyer.evince.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;

import java.util.function.Consumer;

public abstract class AbstractHttpResponse implements HttpResponse {

    private final ObjectMapper jsonObjectMapper;

    protected AbstractHttpResponse(ObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }

    protected abstract void writeResponse();

    @Override
    public HttpResponse cookie(String name, String value, Consumer<Cookie> cookieOptions) {
        Cookie cookie = new DefaultCookie(name, value);
        cookieOptions.accept(cookie);
        cookie(name, cookie);
        return this;
    }

    @Override
    public HttpResponse clearCookie(String name) {
        DefaultCookie cookie = new DefaultCookie(name, "");
        cookie.setMaxAge(0);
        cookie.setDiscard(true);
        cookie(name, cookie);
        return this;
    }

    @Override
    public HttpResponse redirect(String url) {
        status(302);
        header("Location", url);
        return this;
    }

    @Override
    public HttpResponse json(int status) {
        type("application/json");
        charset("UTF-8");
        send(status);
        return this;
    }

    @Override
    public HttpResponse json(int status, Object body) {
        type("application/json");
        charset("UTF-8");

        if (body instanceof String) {
            send(status, (String) body);
        } else {
            try {
                send(status, jsonObjectMapper.writeValueAsString(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return this;

    }

    @Override
    public HttpResponse json(String body) {
        type("application/json");
        charset("UTF-8");
        send(body);
        return this;

    }

    @Override
    public HttpResponse send(int status) {
        status(status);
        writeResponse();
        return this;
    }

    @Override
    public HttpResponse send(int status, String body) {
        status(status);
        send(body);
        return this;
    }
}
