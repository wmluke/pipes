package net.bunselmeyer.hitch.servlet;

import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.hitch.app.AbstractResponse;
import net.bunselmeyer.hitch.app.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class HttpServletAdapterResponse extends AbstractResponse {

    private final HttpServletResponse httpResponse;

    public HttpServletAdapterResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public boolean isCommitted() {
        return httpResponse.isCommitted();
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
    public Response redirect(String url) {
        try {
            httpResponse.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public Response send(String body) {
        try {
            httpResponse.getWriter().append(body);
            writeResponse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    protected void writeResponse() {
        try {
            httpResponse.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
