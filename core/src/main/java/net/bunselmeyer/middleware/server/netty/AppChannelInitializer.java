package net.bunselmeyer.middleware.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

public class AppChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final App<HttpRequest, HttpResponse, ?> app;

    public AppChannelInitializer(App<HttpRequest, HttpResponse, ?> app) {
        this.app = app;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline p = ch.pipeline();

        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        p.addLast(new FooHandler());
        p.addLast(new MiddlewareChanelHandler(app));


    }
}
