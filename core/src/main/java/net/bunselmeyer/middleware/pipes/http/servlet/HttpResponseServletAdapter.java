package net.bunselmeyer.middleware.pipes.http.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpResponse;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        for (String name : httpResponse.getHeaderNames()) {
            headers.put(name, httpResponse.getHeader(name));
        }
        return headers;
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
    public PrintWriter writer() throws IOException {
        return httpResponse.getWriter();
    }

    @Override
    public OutputStream outputStream() throws IOException {
        return httpResponse.getOutputStream();
    }

    @Override
    protected void writeResponse() {
        try {
            httpResponse.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpServletResponse delegate() {
        return httpResponse;
    }

}
