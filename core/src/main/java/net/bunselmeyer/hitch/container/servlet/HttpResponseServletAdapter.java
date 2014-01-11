package net.bunselmeyer.hitch.container.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.hitch.http.AbstractHttpResponse;
import net.bunselmeyer.hitch.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class HttpResponseServletAdapter extends AbstractHttpResponse {

    private final HttpServletResponse httpResponse;

    public HttpResponseServletAdapter(HttpServletResponse httpResponse, ObjectMapper jsonObjectMapper) {
        super(jsonObjectMapper);
        this.httpResponse = httpResponse;
    }

    @Override
    public boolean isCommitted() {
        return httpResponse.isCommitted();
    }

    @Override
    public HttpResponse status(int status) {
        httpResponse.setStatus(status);
        return this;
    }

    @Override
    public Integer status() {
        return httpResponse.getStatus();
    }

    @Override
    public HttpResponse header(String name, String value) {
        httpResponse.setHeader(name, value);
        return this;
    }

    @Override
    public String header(String name) {
        return httpResponse.getHeader(name);
    }

    @Override
    public HttpResponse cookie(String name, Cookie value) {
        httpResponse.addCookie(HttpResponse.servletCookie(value));
        return this;
    }

    @Override
    public HttpResponse redirect(String url) {
        try {
            httpResponse.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public HttpResponse charset(String charset) {
        httpResponse.setCharacterEncoding(charset);
        return this;
    }

    @Override
    public HttpResponse charset(Charset charset) {
        httpResponse.setCharacterEncoding(charset.name());
        return this;
    }

    @Override
    public Charset charset() {
        return Charset.forName(httpResponse.getCharacterEncoding());
    }

    @Override
    public HttpResponse type(String type) {
        httpResponse.setContentType(type);
        return this;
    }

    @Override
    public String type() {
        return httpResponse.getContentType();
    }

    @Override
    public HttpResponse send(String body) {
        try {
            if (status() < 200) {
                status(200);
            }
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
