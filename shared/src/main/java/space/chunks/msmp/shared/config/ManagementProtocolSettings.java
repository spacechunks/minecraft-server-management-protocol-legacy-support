package space.chunks.msmp.shared.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ManagementProtocolSettings {
    private final boolean enabled;
    private final String host;
    private final int port;
    private final String secret;
    private final List<String> allowedOrigins;
    private final boolean tlsEnabled;
    private final Path tlsKeystore;
    private final String tlsKeystorePassword;

    public ManagementProtocolSettings(boolean enabled, String host, int port) {
        this(builder().enabled(enabled).host(host).port(port));
    }

    private ManagementProtocolSettings(Builder builder) {
        this.enabled = builder.enabled;
        this.host = Objects.requireNonNull(builder.host, "host");
        this.port = builder.port;
        this.secret = builder.secret;
        this.allowedOrigins = Collections.unmodifiableList(new ArrayList<String>(builder.allowedOrigins));
        this.tlsEnabled = builder.tlsEnabled;
        this.tlsKeystore = builder.tlsKeystore;
        this.tlsKeystorePassword = builder.tlsKeystorePassword;
    }

    public static ManagementProtocolSettings defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSecret() {
        return secret;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public Path getTlsKeystore() {
        return tlsKeystore;
    }

    public String getTlsKeystorePassword() {
        return tlsKeystorePassword;
    }

    public static final class Builder {
        private boolean enabled;
        private String host = "localhost";
        private int port;
        private String secret;
        private List<String> allowedOrigins = new ArrayList<String>();
        private boolean tlsEnabled;
        private Path tlsKeystore;
        private String tlsKeystorePassword;

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("port must be between 0 and 65535");
            }

            this.port = port;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Builder allowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins == null
                ? new ArrayList<String>()
                : new ArrayList<String>(allowedOrigins);
            return this;
        }

        public Builder tlsEnabled(boolean tlsEnabled) {
            this.tlsEnabled = tlsEnabled;
            return this;
        }

        public Builder tlsKeystore(Path tlsKeystore) {
            this.tlsKeystore = tlsKeystore;
            return this;
        }

        public Builder tlsKeystorePassword(String tlsKeystorePassword) {
            this.tlsKeystorePassword = tlsKeystorePassword;
            return this;
        }

        public ManagementProtocolSettings build() {
            return new ManagementProtocolSettings(this);
        }
    }
}
