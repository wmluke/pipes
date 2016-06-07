package net.bunselmeyer.middleware.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderUtil.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

class MiddlewareChanelHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(MiddlewareChanelHandler.class);

    private static final HttpDataFactory factory =
        new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    private final RunnableApp<HttpRequest, HttpResponse> app;

    MiddlewareChanelHandler(RunnableApp<HttpRequest, HttpResponse> app) {
        this.app = app;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof DefaultHttpRequest) {
            DefaultHttpRequest request = (DefaultHttpRequest) msg;

            if (is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }


//            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);

//            decoder.getBodyHttpData()

            //decoder.getBodyHttpDatas()


//            ObjectMapper jsonMapper = app.configuration(ObjectMapper.class);
//            ObjectMapper xmlMapper = app.configuration(ObjectMapper.class, App.XML_MAPPER_NAME);

            //AbstractHttpRequest req = new HttpRequestNettyAdapter(request, jsonMapper, xmlMapper);
            //HttpResponseNettyAdapter res = new HttpResponseNettyAdapter(ctx, isKeepAlive(request), jsonMapper);

            //app.run(req, res, null);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();


            logger.error("Unsupported HttpMessage {}", msg);
        }
    }
}
