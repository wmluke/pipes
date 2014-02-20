package app.configure;

import org.eclipse.jetty.server.session.HashSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SessionManagerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SessionManagerConfig.class);

    public static void configure(HashSessionManager sessionManager) {
        sessionManager.setHttpOnly(true);
        sessionManager.setSessionCookie("APPSESSIONID");
        try {
            Path dir = Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir"), "app-session-store"));
            sessionManager.setStoreDirectory(dir.toFile());
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
