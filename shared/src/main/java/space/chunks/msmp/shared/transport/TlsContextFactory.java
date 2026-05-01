package space.chunks.msmp.shared.transport;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public final class TlsContextFactory {
    public static final String KEYSTORE_PASSWORD_ENV = "MINECRAFT_MANAGEMENT_TLS_KEYSTORE_PASSWORD";
    public static final String KEYSTORE_PASSWORD_PROPERTY = "management.tls.keystore.password";

    private TlsContextFactory() {
    }

    public static SSLContext create(Path keystorePath, String configuredPassword) {
        if (keystorePath == null) {
            throw new IllegalArgumentException("TLS is enabled but keystore is not configured");
        }

        if (!Files.isRegularFile(keystorePath)) {
            throw new IllegalArgumentException("Supplied keystore is not a file or does not exist: " + keystorePath);
        }

        String password = resolvePassword(configuredPassword);
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream input = Files.newInputStream(keystorePath)) {
                keyStore.load(input, password.toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), null, null);
            return context;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to configure TLS for the server management protocol", exception);
        }
    }

    public static String resolvePassword(String configuredPassword) {
        String envPassword = System.getenv(KEYSTORE_PASSWORD_ENV);
        if (envPassword != null) {
            return envPassword;
        }

        String propertyPassword = System.getProperty(KEYSTORE_PASSWORD_PROPERTY);
        if (propertyPassword != null) {
            return propertyPassword;
        }

        return configuredPassword == null ? "" : configuredPassword;
    }
}
