package net.bunselmeyer.middleware.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

class AppChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final RunnableApp<HttpRequest, HttpResponse> app;

    AppChannelInitializer(RunnableApp<HttpRequest, HttpResponse> app) {
        this.app = app;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //p.addLast("ssl", new SslHandler(engine));

        p.addLast("decoder", new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast("aggregator", new HttpObjectAggregator(1048576));
        p.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        p.addLast("deflater", new HttpContentCompressor());

        //p.addLast("codec", new HttpServerCodec());
        p.addLast("handler", new MiddlewareChanelHandler(app));


    }
}
