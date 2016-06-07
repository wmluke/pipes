package net.bunselmeyer.middleware.pipes.http.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequestBody;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

class HttpRequestServletBody extends AbstractHttpRequestBody implements HttpRequest.Body {

    private final HttpServletRequest httpRequest;

    HttpRequestServletBody(HttpServletRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        super(jsonMapper, xmlMapper);
        this.httpRequest = httpRequest;
    }

    @Override
    public InputStream asInputStream() {
        try {
            return httpRequest.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
