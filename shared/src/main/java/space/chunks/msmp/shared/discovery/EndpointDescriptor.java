package space.chunks.msmp.shared.discovery;

import java.util.Objects;
import space.chunks.msmp.shared.protocol.MethodName;

public class EndpointDescriptor {
    private final MethodName name;
    private final EndpointGroup group;
    private final String description;
    private final ParameterDescriptor parameter;
    private final boolean discoverable;
    private final boolean availableBeforeServerStart;

    protected EndpointDescriptor(
        MethodName name,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        boolean discoverable,
        boolean availableBeforeServerStart
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.group = Objects.requireNonNull(group, "group");
        this.description = Objects.requireNonNull(description, "description");
        this.parameter = parameter;
        this.discoverable = discoverable;
        this.availableBeforeServerStart = availableBeforeServerStart;
    }

    public MethodName name() {
        return name;
    }

    public EndpointGroup group() {
        return group;
    }

    public String description() {
        return description;
    }

    public ParameterDescriptor parameter() {
        return parameter;
    }

    public boolean discoverable() {
        return discoverable;
    }

    public boolean availableBeforeServerStart() {
        return availableBeforeServerStart;
    }
}
