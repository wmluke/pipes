package net.bunselmeyer.hitch.middleware;

import com.codahale.dropwizard.servlets.assets.AssetServlet;
import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Consumer;

public class StaticMiddleware {

    private StaticMiddleware() {
    }

    public static class Hitch {

        private Hitch() {
        }

        public static Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> staticFiles(Path root, String uriPath, Consumer<Options> block) {
            Options options = new Options();
            block.accept(options);
            return new StaticServlet(root.toString(), uriPath, options)::run;
        }
    }


    public static class Evince {

        private Evince() {
        }

        public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> staticFiles(Path root, String uriPath, Consumer<Options> block) {
            return (request, response) -> Hitch.staticFiles(root, uriPath, block).run(request.delegate(), response.delegate());
        }
    }

    public static class Options {

        /**
         * Default file name, defaults to 'index.html'
         */
        public String index = "index.html";

        /**
         * the default character set
         */
        public Charset defaultCharset = Charset.forName("UTF-8");
    }

    /**
     * Thank you dropwizard for serving static assets!
     */
    private static class StaticServlet extends AssetServlet {

        public StaticServlet(String resourcePath, String uriPath, Options options) {
            super(resourcePath, uriPath, options.index, options.defaultCharset);
        }

        /**
         * Wrapped the protected service method to use HttpServletRequest and HttpServletResponse.
         */
        public void run(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            super.service(request, response);
        }
    }
}
