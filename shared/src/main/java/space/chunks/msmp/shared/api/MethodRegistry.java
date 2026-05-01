package space.chunks.msmp.shared.api;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import space.chunks.msmp.shared.discovery.MethodDescriptor;
import space.chunks.msmp.shared.protocol.MethodName;

public final class MethodRegistry {
    private static final MethodRegistry EMPTY = new MethodRegistry(Collections.<MethodName, RegisteredMethod>emptyMap());

    private final Map<MethodName, RegisteredMethod> methods;

    private MethodRegistry(Map<MethodName, RegisteredMethod> methods) {
        this.methods = Collections.unmodifiableMap(new LinkedHashMap<MethodName, RegisteredMethod>(methods));
    }

    public static MethodRegistry empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<RegisteredMethod> find(String methodName) {
        return find(MethodName.raw(methodName));
    }

    public Optional<RegisteredMethod> find(MethodName methodName) {
        return Optional.ofNullable(methods.get(methodName));
    }

    public Collection<RegisteredMethod> methods() {
        return methods.values();
    }

    public int size() {
        return methods.size();
    }

    public static final class Builder {
        private final Map<MethodName, RegisteredMethod> methods = new LinkedHashMap<MethodName, RegisteredMethod>();

        private Builder() {
        }

        public Builder add(MethodDescriptor descriptor, MethodHandler handler) {
            RegisteredMethod method = new RegisteredMethod(descriptor, handler);
            methods.put(method.name(), method);
            return this;
        }

        public MethodRegistry build() {
            return new MethodRegistry(methods);
        }
    }
}
