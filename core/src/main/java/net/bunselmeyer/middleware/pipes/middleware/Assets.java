package net.bunselmeyer.middleware.pipes.middleware;

import com.codahale.dropwizard.servlets.assets.ResourceURL;
import com.google.common.base.CharMatcher;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import org.eclipse.jetty.http.MimeTypes;

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
public class Assets implements Middleware.StandardMiddleware1<HttpRequest, HttpResponse> {

    private static final CharMatcher SLASHES = CharMatcher.is('/');
    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;

    private final String resourcePath;
    private final String uriPath;
    private final String indexFile;
    private final Charset defaultCharset;
    private final boolean handleNotFound;
    private final MimeTypes mimeTypes = new MimeTypes();


    public static Assets mountResourceDir(String resourcePath, String uriPath, Consumer<Options> block) {
        Options options = new Options();
        options.resourcePath = resourcePath;
        options.uriPath = uriPath;
        block.accept(options);
        return new Assets(options);
    }

    public static Assets mountResourceDir(String resourcePath, String uriPath) {
        Options options = new Options();
        options.resourcePath = resourcePath;
        options.uriPath = uriPath;
        return new Assets(options);
    }

    private Assets(Options options) {
        final String trimmedPath = SLASHES.trimFrom(options.resourcePath);
        this.resourcePath = trimmedPath.isEmpty() ? trimmedPath : trimmedPath + '/';
        final String trimmedUri = SLASHES.trimTrailingFrom(options.uriPath);
        this.uriPath = trimmedUri.isEmpty() ? "/" : trimmedUri;
        this.indexFile = options.index;
        this.defaultCharset = options.defaultCharset;
        this.handleNotFound = options.handleNotFound;
    }

    @Override
    public void run(HttpRequest req, HttpResponse resp) throws Exception {
        try {
            final StringBuilder builder = new StringBuilder("");
            if (req.path() != null) {
                builder.append(req.path());
            }
            final CachedAsset cachedAsset = loadAsset(builder.toString());
            if (cachedAsset == null) {
                if (handleNotFound) {
                    resp.send(HttpServletResponse.SC_NOT_FOUND);
                }
                return;
            }

            if (isCachedClientSide(req, cachedAsset)) {
                resp.send(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            resp.header(HttpHeaders.LAST_MODIFIED, Long.toString(cachedAsset.getLastModifiedTime()));
            resp.header(HttpHeaders.ETAG, cachedAsset.getETag());

            final String mimeTypeOfExtension = mimeTypes.getMimeByExtension(req.uri());
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

            resp.type(mediaType.type() + '/' + mediaType.subtype());

            if (mediaType.charset().isPresent()) {
                resp.charset(mediaType.charset().get().toString());
            }

            resp.sendOutput((output -> {
                output.write(cachedAsset.getResource());
            }));
        } catch (RuntimeException e) {
            // ignored b/c we want to give the next middle a whack at the request
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

    private boolean isCachedClientSide(HttpRequest req, CachedAsset cachedAsset) {
        return req.header(HttpHeaders.IF_NONE_MATCH).equalTo(cachedAsset.getETag()) ||
            (req.dateHeader(HttpHeaders.IF_MODIFIED_SINCE).orElse(-1L) >= cachedAsset.getLastModifiedTime());
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

        byte[] getResource() {
            return resource;
        }

        String getETag() {
            return eTag;
        }

        long getLastModifiedTime() {
            return lastModifiedTime;
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
