package space.chunks.msmp.shared.protocol;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class PlatformCapabilities {
    private final Set<MethodName> supportedMethods;

    private PlatformCapabilities(Set<MethodName> supportedMethods) {
        this.supportedMethods = Collections.unmodifiableSet(new LinkedHashSet<MethodName>(supportedMethods));
    }

    public static PlatformCapabilities discoveryOnly() {
        return builder().method(MinecraftProtocolCatalog.RPC_DISCOVER.name()).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean supports(MethodName methodName) {
        return supportedMethods.contains(methodName);
    }

    public Set<MethodName> supportedMethods() {
        return supportedMethods;
    }

    public static final class Builder {
        private final Set<MethodName> supportedMethods = new LinkedHashSet<MethodName>();

        private Builder() {
        }

        public Builder method(MethodName methodName) {
            supportedMethods.add(methodName);
            return this;
        }

        public Builder minecraft(String path) {
            return method(MethodName.minecraft(path));
        }

        public Builder notification(String path) {
            return method(MethodName.notification(path));
        }

        public Builder notifications(String basePath, String... children) {
            notification(basePath);
            for (String child : children) {
                notification(basePath + "/" + child);
            }
            return this;
        }

        public Builder group(String basePath, String... children) {
            minecraft(basePath);
            for (String child : children) {
                minecraft(basePath + "/" + child);
            }
            return this;
        }

        public Builder serverSetting(String key, boolean writable) {
            minecraft("serversettings/" + key);
            if (writable) {
                minecraft("serversettings/" + key + "/set");
            }
            return this;
        }

        public PlatformCapabilities build() {
            return new PlatformCapabilities(supportedMethods);
        }
    }
}
