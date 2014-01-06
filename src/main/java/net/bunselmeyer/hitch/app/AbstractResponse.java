package net.bunselmeyer.hitch.app;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;

public abstract class AbstractResponse implements Response {

    protected abstract void writeResponse();

    @Override
    public Response cookie(String name, String value, Options<Cookie> cookieOptions) {
        Cookie cookie = new DefaultCookie(name, value);
        cookieOptions.build(cookie);
        cookie(name, cookie);
        return this;
    }

    @Override
    public Response clearCookie(String name) {
        DefaultCookie cookie = new DefaultCookie(name, null);
        cookie.setDiscard(true);
        cookie.setMaxAge(-1);
        cookie(name, cookie);
        return this;
    }

    @Override
    public Response redirect(String url) {
        status(302);
        header("Location", url);
        return this;
    }

    @Override
    public Response json(int status) {
        type("application/json");
        charset("UTF-8");
        send(status);
        return this;
    }

    @Override
    public Response json(int status, String body) {
        type("application/json");
        charset("UTF-8");
        send(status, body);
        return this;

    }

    @Override
    public Response json(String body) {
        type("application/json");
        charset("UTF-8");
        return this;

    }

    @Override
    public Response send(int status) {
        status(status);
        writeResponse();
        return this;
    }

    @Override
    public Response send(int status, String body) {
        status(status);
        send(body);
        return this;
    }
}
