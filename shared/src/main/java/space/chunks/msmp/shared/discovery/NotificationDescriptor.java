package space.chunks.msmp.shared.discovery;

import space.chunks.msmp.shared.protocol.MethodName;

public final class NotificationDescriptor extends EndpointDescriptor {
    public NotificationDescriptor(MethodName name, EndpointGroup group, String description, ParameterDescriptor parameter) {
        this(name, group, description, parameter, true, false);
    }

    public NotificationDescriptor(
        MethodName name,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        boolean discoverable,
        boolean availableBeforeServerStart
    ) {
        super(name, group, description, parameter, discoverable, availableBeforeServerStart);
    }
}
