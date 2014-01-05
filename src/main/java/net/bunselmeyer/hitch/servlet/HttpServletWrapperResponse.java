package net.bunselmeyer.hitch.servlet;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import net.bunselmeyer.hitch.app.Options;
import net.bunselmeyer.hitch.app.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpServletWrapperResponse implements Response {

    private final HttpServletResponse httpResponse;

    public HttpServletWrapperResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public Response status(int status) {
        httpResponse.setStatus(status);
        return this;
    }

    @Override
    public Integer status() {
        return httpResponse.getStatus();
    }

    @Override
    public Response header(String name, String value) {
        httpResponse.setHeader(name, value);
        return this;
    }

    @Override
    public String header(String name) {
        return httpResponse.getHeader(name);
    }

    @Override
    public Response cookie(String name, Cookie value) {
        httpResponse.addCookie(Response.servletCookie(value));
        return this;
    }


    @Override
    public Response cookie(String name, String value, Options<Cookie> cookieOptions) {
        Cookie cookie = new DefaultCookie(name, value);
        cookieOptions.build(cookie);
        httpResponse.addCookie(Response.servletCookie(cookie));
        return this;
    }

    @Override
    public Cookie cookie(String name) {
        return null;
    }

    @Override
    public Response clearCookie(String name) {
        return null;
    }

    @Override
    public Response redirect(int status, String url) {
        return null;
    }

    @Override
    public Response redirect(String url) throws IOException {
        httpResponse.sendRedirect(url);
        return this;
    }

    @Override
    public Response charset(String charset) {
        httpResponse.setCharacterEncoding(charset);
        return this;
    }

    @Override
    public Response charset(Charset charset) {
        httpResponse.setCharacterEncoding(charset.name());
        return this;
    }

    @Override
    public Charset charset() {
        return Charset.forName(httpResponse.getCharacterEncoding());
    }

    @Override
    public Response type(String type) {
        httpResponse.setContentType(type);
        return this;
    }

    @Override
    public String type() {
        return httpResponse.getContentType();
    }

    @Override
    public Response send(int status) throws IOException {
        status(status);
        httpResponse.flushBuffer();
        return this;
    }

    @Override
    public Response send(int status, String body) throws IOException {
        status(status);
        httpResponse.getWriter().append(body);
        httpResponse.flushBuffer();
        return this;

    }

    @Override
    public Response send(String body) throws IOException {
        httpResponse.getWriter().append(body);
        httpResponse.flushBuffer();
        return this;

    }

    @Override
    public Response json(int status) throws IOException {
        type("application/json");
        charset("UTF-8");
        send(status);
        return this;
    }

    @Override
    public Response json(int status, String body) throws IOException {
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
    public String body() {
        return null;
    }

    @Override
    public Map<String, String> headers() {
        return null;
    }

    @Override
    public Map<String, Cookie> cookies() {
        return null;
    }
}
