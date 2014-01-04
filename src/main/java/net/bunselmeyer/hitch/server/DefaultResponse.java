package net.bunselmeyer.hitch.server;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultResponse implements Response {


    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Cookie> cookies = new HashMap<>();

    private Integer status;
    private Charset charset = Charset.forName("UTF-8");
    private String type = "text/html";
    private String body; // TODO: change body type to stream or byte array

    public DefaultResponse() {
    }

    @Override
    public Response status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public Integer status() {
        return status;
    }

    @Override
    public Response header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public String header(String name) {
        return null;
    }

    @Override
    public Map<String, String> headers() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public Response cookie(String name, Cookie value) {
        cookies.put(name, value);
        return this;
    }

    @Override
    public Cookie cookie(String name) {
        return cookies.get(name);
    }

    @Override
    public Map<String, Cookie> cookies() {
        return Collections.unmodifiableMap(cookies);
    }

    @Override
    public Response clearCookie(String name) {
        DefaultCookie cookie = new DefaultCookie(name, null);
        cookie.setDiscard(true);
        cookie.setMaxAge(-1);
        cookies.put(name, cookie);
        return this;
    }

    @Override
    public Response redirect(int status, String url) {
        this.status = status;
        headers.put("Location", url);
        return this;
    }

    @Override
    public Response redirect(String url) {
        redirect(302, url);
        return this;
    }

    @Override
    public Response charset(String charset) {
        this.charset = Charset.forName(charset);
        return this;
    }

    @Override
    public Response charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public Charset charset() {
        return charset;
    }

    @Override
    public Response type(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public Response send(int status) {
        this.status = status;
        return this;
    }

    @Override
    public Response send(int status, String body) {
        this.status = status;
        this.body = body;
        return this;
    }

    @Override
    public Response send(String body) {
        this.body = body;
        return this;
    }

    @Override
    public Response json(int status) {
        this.status = status;
        this.type = "application/json";
        return this;
    }

    @Override
    public Response json(int status, String body) {
        this.status = status;
        this.type = "application/json";
        this.body = body;
        return this;
    }

    @Override
    public Response json(String body) {
        this.type = "application/json";
        this.body = body;
        return this;
    }
}
