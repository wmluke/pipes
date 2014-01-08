package net.bunselmeyer.hitch.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;

import java.util.function.Consumer;

public abstract class AbstractHttpResponse implements HttpResponse {

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
        DefaultCookie cookie = new DefaultCookie(name, null);
        cookie.setDiscard(true);
        cookie.setMaxAge(-1);
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
    public HttpResponse json(int status, String body) {
        type("application/json");
        charset("UTF-8");
        send(status, body);
        return this;

    }

    @Override
    public HttpResponse json(String body) {
        type("application/json");
        charset("UTF-8");
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
