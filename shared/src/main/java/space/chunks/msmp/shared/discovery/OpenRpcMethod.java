package space.chunks.msmp.shared.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class OpenRpcMethod {
    private final String name;
    private final String description;
    private final List<ParameterDescriptor> params;
    private final ResultDescriptor result;

    public OpenRpcMethod(EndpointDescriptor descriptor) {
        this.name = descriptor.name().value();
        this.description = descriptor.description();
        this.params = descriptor.parameter() == null
            ? Collections.<ParameterDescriptor>emptyList()
            : Collections.singletonList(descriptor.parameter());
        this.result = descriptor instanceof MethodDescriptor
            ? ((MethodDescriptor) descriptor).result()
            : null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ParameterDescriptor> getParams() {
        return params;
    }

    public ResultDescriptor getResult() {
        return result;
    }
}
