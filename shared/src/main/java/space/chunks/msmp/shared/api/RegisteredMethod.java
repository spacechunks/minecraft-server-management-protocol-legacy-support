package space.chunks.msmp.shared.api;

import java.util.Objects;
import space.chunks.msmp.shared.discovery.MethodDescriptor;
import space.chunks.msmp.shared.protocol.MethodName;

public final class RegisteredMethod {
    private final MethodDescriptor descriptor;
    private final MethodHandler handler;

    public RegisteredMethod(MethodDescriptor descriptor, MethodHandler handler) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.handler = Objects.requireNonNull(handler, "handler");
    }

    public MethodName name() {
        return descriptor.name();
    }

    public MethodDescriptor descriptor() {
        return descriptor;
    }

    public MethodHandler handler() {
        return handler;
    }
}
