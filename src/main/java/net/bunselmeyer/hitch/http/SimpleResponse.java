package net.bunselmeyer.hitch.http;

import io.netty.handler.codec.http.Cookie;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleResponse extends AbstractResponse {


    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Cookie> cookies = new HashMap<>();

    private Integer status;
    private Charset charset = Charset.forName("UTF-8");
    private String type = "text/html";
    private String body; // TODO: change body type to stream or byte array
    private boolean committed;

    public SimpleResponse() {
    }

    @Override
    public boolean isCommitted() {
        return committed;
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

    public Map<String, String> headers() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public Response cookie(String name, Cookie value) {
        cookies.put(name, value);
        return this;
    }

    public Cookie cookie(String name) {
        return cookies.get(name);
    }

    public Map<String, Cookie> cookies() {
        return Collections.unmodifiableMap(cookies);
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

    public String body() {
        return body;
    }

    @Override
    public Response send(String body) {
        this.body = body;
        writeResponse();
        return this;
    }

    @Override
    protected void writeResponse() {
        this.committed = true;
    }
}
