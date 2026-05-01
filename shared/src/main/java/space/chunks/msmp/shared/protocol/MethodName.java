package space.chunks.msmp.shared.protocol;

import java.util.Objects;

public final class MethodName {
    public static final String MINECRAFT_NAMESPACE = "minecraft";
    public static final String RPC_DISCOVER = "rpc.discover";

    private final String value;

    private MethodName(String value) {
        this.value = value;
    }

    public static MethodName raw(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("method name cannot be blank");
        }

        return new MethodName(value);
    }

    public static MethodName minecraft(String path) {
        return namespaced(MINECRAFT_NAMESPACE, path);
    }

    public static MethodName notification(String path) {
        return minecraft("notification/" + normalizePath(path));
    }

    public static MethodName namespaced(String namespace, String path) {
        if (namespace == null || namespace.trim().isEmpty()) {
            throw new IllegalArgumentException("namespace cannot be blank");
        }

        return new MethodName(namespace + ":" + normalizePath(path));
    }

    private static String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("path cannot be blank");
        }

        return path.charAt(0) == '/' ? path.substring(1) : path;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof MethodName)) {
            return false;
        }

        MethodName that = (MethodName) other;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
