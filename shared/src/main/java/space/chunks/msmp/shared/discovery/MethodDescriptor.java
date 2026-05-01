package space.chunks.msmp.shared.discovery;

import space.chunks.msmp.shared.protocol.MethodName;

public final class MethodDescriptor extends EndpointDescriptor {
    private final ResultDescriptor result;
    private final boolean runOnMainThread;

    public MethodDescriptor(
        MethodName name,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        ResultDescriptor result
    ) {
        this(name, group, description, parameter, result, true, true, false);
    }

    public MethodDescriptor(
        MethodName name,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        ResultDescriptor result,
        boolean discoverable,
        boolean runOnMainThread,
        boolean availableBeforeServerStart
    ) {
        super(name, group, description, parameter, discoverable, availableBeforeServerStart);
        this.result = result;
        this.runOnMainThread = runOnMainThread;
    }

    public ResultDescriptor result() {
        return result;
    }

    public boolean runOnMainThread() {
        return runOnMainThread;
    }
}
