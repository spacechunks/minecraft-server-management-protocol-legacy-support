package space.chunks.msmp.shared.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class ManagementSettingsProperties {
    public static final String ENABLED = "management-server-enabled";
    public static final String HOST = "management-server-host";
    public static final String PORT = "management-server-port";
    public static final String SECRET = "management-server-secret";
    public static final String ALLOWED_ORIGINS = "management-server-allowed-origins";
    public static final String TLS_ENABLED = "management-server-tls-enabled";
    public static final String TLS_KEYSTORE = "management-server-tls-keystore";
    public static final String TLS_KEYSTORE_PASSWORD = "management-server-tls-keystore-password";

    private ManagementSettingsProperties() {
    }

    public static List<String> keys() {
        return Arrays.asList(
            ENABLED,
            HOST,
            PORT,
            SECRET,
            ALLOWED_ORIGINS,
            TLS_ENABLED,
            TLS_KEYSTORE,
            TLS_KEYSTORE_PASSWORD
        );
    }

    public static boolean containsManagementSetting(Properties properties) {
        for (String key : keys()) {
            if (properties.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public static Properties defaults() {
        Properties properties = new Properties();
        properties.setProperty(ENABLED, "false");
        properties.setProperty(HOST, "localhost");
        properties.setProperty(PORT, "0");
        properties.setProperty(SECRET, "");
        properties.setProperty(ALLOWED_ORIGINS, "");
        properties.setProperty(TLS_ENABLED, "false");
        properties.setProperty(TLS_KEYSTORE, "");
        properties.setProperty(TLS_KEYSTORE_PASSWORD, "");
        return properties;
    }

    public static ManagementProtocolSettings from(Properties properties) {
        Properties defaults = defaults();
        defaults.putAll(properties);

        String keystore = defaults.getProperty(TLS_KEYSTORE, "").trim();
        Path keystorePath = keystore.isEmpty() ? null : Paths.get(keystore);

        return ManagementProtocolSettings.builder()
            .enabled(Boolean.parseBoolean(defaults.getProperty(ENABLED)))
            .host(defaults.getProperty(HOST))
            .port(parsePort(defaults.getProperty(PORT)))
            .secret(defaults.getProperty(SECRET))
            .allowedOrigins(parseCsv(defaults.getProperty(ALLOWED_ORIGINS)))
            .tlsEnabled(Boolean.parseBoolean(defaults.getProperty(TLS_ENABLED)))
            .tlsKeystore(keystorePath)
            .tlsKeystorePassword(defaults.getProperty(TLS_KEYSTORE_PASSWORD))
            .build();
    }

    public static List<String> parseCsv(String value) {
        List<String> values = new ArrayList<String>();
        if (value == null || value.trim().isEmpty()) {
            return values;
        }

        String[] parts = value.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        return values;
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid " + PORT + " value: " + value, exception);
        }
    }
}
