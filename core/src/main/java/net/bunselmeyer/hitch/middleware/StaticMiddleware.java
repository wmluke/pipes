package net.bunselmeyer.hitch.middleware;

import com.codahale.dropwizard.servlets.assets.ResourceURL;
import com.google.common.base.CharMatcher;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Thank you dropwizard for serving static assets!
 * Pulled from com.codahale.dropwizard.servlets.assets.AssetServlet
 */
public class StaticMiddleware implements Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> {

    private static final Logger logger = LoggerFactory.getLogger(StaticMiddleware.class);

    private static final CharMatcher SLASHES = CharMatcher.is('/');
    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;

    private final String resourcePath;
    private final String uriPath;
    private final String indexFile;
    private final Charset defaultCharset;
    private final boolean handleNotFound;


    public StaticMiddleware(Options options) {
        final String trimmedPath = SLASHES.trimFrom(options.resourcePath);
        this.resourcePath = trimmedPath.isEmpty() ? trimmedPath : trimmedPath + '/';
        final String trimmedUri = SLASHES.trimTrailingFrom(options.uriPath);
        this.uriPath = trimmedUri.isEmpty() ? "/" : trimmedUri;
        this.indexFile = options.index;
        this.defaultCharset = options.defaultCharset;
        this.handleNotFound = options.handleNotFound;
    }

    @Override
    public void run(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            final StringBuilder builder = new StringBuilder(req.getServletPath());
            if (req.getPathInfo() != null) {
                builder.append(req.getPathInfo());
            }
            final CachedAsset cachedAsset = loadAsset(builder.toString());
            if (cachedAsset == null) {
                if (handleNotFound) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                return;
            }

            if (isCachedClientSide(req, cachedAsset)) {
                resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            resp.setDateHeader(HttpHeaders.LAST_MODIFIED, cachedAsset.getLastModifiedTime());
            resp.setHeader(HttpHeaders.ETAG, cachedAsset.getETag());

            final String mimeTypeOfExtension = req.getServletContext()
                    .getMimeType(req.getRequestURI());
            MediaType mediaType = DEFAULT_MEDIA_TYPE;

            if (mimeTypeOfExtension != null) {
                try {
                    mediaType = MediaType.parse(mimeTypeOfExtension);
                    if (defaultCharset != null && mediaType.is(MediaType.ANY_TEXT_TYPE)) {
                        mediaType = mediaType.withCharset(defaultCharset);
                    }
                } catch (IllegalArgumentException ignore) {
                }
            }

            resp.setContentType(mediaType.type() + '/' + mediaType.subtype());

            if (mediaType.charset().isPresent()) {
                resp.setCharacterEncoding(mediaType.charset().get().toString());
            }

            try (ServletOutputStream output = resp.getOutputStream()) {
                output.write(cachedAsset.getResource());
            }
        } catch (RuntimeException e) {
            // ignored
            logger.warn("!!!!! " + e.getMessage());
        }
    }

    private CachedAsset loadAsset(String key) throws URISyntaxException, IOException {
        checkArgument(key.startsWith(uriPath));
        final String requestedResourcePath = SLASHES.trimFrom(key.substring(uriPath.length()));
        final String absoluteRequestedResourcePath = SLASHES.trimFrom(this.resourcePath + requestedResourcePath);

        URL requestedResourceURL = Resources.getResource(absoluteRequestedResourcePath);
        if (ResourceURL.isDirectory(requestedResourceURL)) {
            if (indexFile != null) {
                requestedResourceURL = Resources.getResource(absoluteRequestedResourcePath + '/' + indexFile);
            } else {
                // directory requested but no index file defined
                return null;
            }
        }

        long lastModified = ResourceURL.getLastModified(requestedResourceURL);
        if (lastModified < 1) {
            // Something went wrong trying to get the last modified time: just use the current time
            lastModified = System.currentTimeMillis();
        }

        // zero out the millis since the date we get back from If-Modified-Since will not have them
        lastModified = (lastModified / 1000) * 1000;
        return new CachedAsset(Resources.toByteArray(requestedResourceURL), lastModified);
    }

    private boolean isCachedClientSide(HttpServletRequest req, CachedAsset cachedAsset) {
        return cachedAsset.getETag().equals(req.getHeader(HttpHeaders.IF_NONE_MATCH)) ||
                (req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= cachedAsset.getLastModifiedTime());
    }

    private static class CachedAsset {
        private final byte[] resource;
        private final String eTag;
        private final long lastModifiedTime;

        private CachedAsset(byte[] resource, long lastModifiedTime) {
            this.resource = resource;
            this.eTag = '"' + Hashing.murmur3_128().hashBytes(resource).toString() + '"';
            this.lastModifiedTime = lastModifiedTime;
        }

        public byte[] getResource() {
            return resource;
        }

        public String getETag() {
            return eTag;
        }

        public long getLastModifiedTime() {
            return lastModifiedTime;
        }
    }


    public static class Hitch {

        private Hitch() {
        }

        public static Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> mountResourceDir(String resourcePath, String uriPath, Consumer<Options> block) {
            Options options = new Options();
            options.resourcePath = resourcePath;
            options.uriPath = uriPath;
            block.accept(options);
            return new StaticMiddleware(options)::run;
        }

        public static Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> mountResourceDir(String resourcePath, String uriPath) {
            Options options = new Options();
            options.resourcePath = resourcePath;
            options.uriPath = uriPath;
            return new StaticMiddleware(options)::run;
        }

    }


    public static class Evince {

        private Evince() {
        }

        public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> mountResourceDir(String resourcePath, String uriPath, Consumer<Options> block) {
            return (request, response) -> Hitch.mountResourceDir(resourcePath, uriPath, block).run(request.delegate(), response.delegate());
        }

        public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> mountResourceDir(String resourcePath, String uriPath) {
            return (request, response) -> Hitch.mountResourceDir(resourcePath, uriPath).run(request.delegate(), response.delegate());
        }
    }

    public static class Options {

        /**
         * the base resource URL from which assets are loaded
         */
        public String resourcePath;

        /**
         * the URI path fragment in which all requests are rooted
         */
        public String uriPath;

        /**
         * Default file name, defaults to 'index.html'
         */
        public String index = "index.html";

        /**
         *
         */
        public boolean handleNotFound = true;

        /**
         * the default character set
         */
        public Charset defaultCharset = Charset.forName("UTF-8");
    }

}
