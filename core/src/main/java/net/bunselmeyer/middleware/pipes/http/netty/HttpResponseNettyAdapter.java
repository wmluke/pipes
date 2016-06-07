package net.bunselmeyer.middleware.pipes.http.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpResponse;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static javax.ws.rs.core.HttpHeaders.*;

public class HttpResponseNettyAdapter extends AbstractHttpResponse {

    private final ChannelHandlerContext ctx;
    private final DefaultFullHttpResponse httpResponse;
    private final boolean keepAlive;
    private Charset charset = Charset.forName("UTF-8");
    private String type = "text/html";
    private boolean committed;


    public HttpResponseNettyAdapter(ChannelHandlerContext ctx, boolean keepAlive, ObjectMapper jsonObjectMapper) {
        super(jsonObjectMapper);
        this.ctx = ctx;
        this.httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        this.keepAlive = keepAlive;
    }

    @Override
    public PrintWriter writer() throws IOException {
        return new PrintWriter(new OutputStreamWriter(outputStream(), charset()));
    }

    @Override
    public OutputStream outputStream() throws IOException {
        return new ByteBufOutputStream(httpResponse.content());
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    @Override
    public HttpResponse status(int status) {
        httpResponse.setStatus(HttpResponseStatus.valueOf(status));
        return this;
    }

    @Override
    public Integer status() {
        return httpResponse.status().code();
    }

    @Override
    public HttpResponse header(String name, String value) {
        httpResponse.headers().set(name, value);
        return this;
    }

    @Override
    public String header(String name) {
        return (String) httpResponse.headers().get(name);
    }

    @Override
    public Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        for (CharSequence name : httpResponse.headers().names()) {
            headers.put((String) name, (String) httpResponse.headers().get(name));
        }
        return headers;
    }

    @Override
    public HttpResponse cookie(String name, Cookie value) {
        header(SET_COOKIE, ServerCookieEncoder.encode(value));
        return this;
    }

    @Override
    public HttpResponse charset(String charset) {
        this.charset = Charset.forName(charset);
        return this;
    }

    @Override
    public HttpResponse charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public Charset charset() {
        return charset;
    }

    @Override
    public HttpResponse type(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public HttpResponse send(String body) {
        httpResponse.content().writeBytes(Unpooled.copiedBuffer(body, charset()));
        writeResponse();
        return this;
    }

    @Override
    public HttpServletResponse delegate() {
        return null;
    }

    @Override
    protected void writeResponse() {
        List<String> contentType = new ArrayList<>();
        contentType.add(StringUtils.trimToNull(type()));
        if (charset() != null) {
            contentType.add("charset=" + charset().name());
        }
        header(CONTENT_TYPE, Joiner.on("; ").skipNulls().join(contentType));
        //httpResponse.headers().setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());

        if (!keepAlive) {
            ctx.write(httpResponse);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        } else {
            httpResponse.headers()
                .setInt(CONTENT_LENGTH, httpResponse.content().readableBytes())
                .set(CONNECTION, KEEP_ALIVE);
            ctx.write(httpResponse);
        }
        committed = true;

    }
}
