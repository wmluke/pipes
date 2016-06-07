package net.bunselmeyer.middleware.pipes.http.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequestBody;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;

import java.io.InputStream;


class HttpRequestNettyBody extends AbstractHttpRequestBody implements HttpRequest.Body {

    private final FullHttpRequest httpRequest;

    HttpRequestNettyBody(FullHttpRequest httpRequest, ObjectMapper jsonMapper, ObjectMapper xmlMapper) {
        super(jsonMapper, xmlMapper);
        this.httpRequest = httpRequest;
    }


    @Override
    public InputStream asInputStream() {
        ByteBuf content = httpRequest.content();
        return new ByteBufInputStream(content);
    }

}
