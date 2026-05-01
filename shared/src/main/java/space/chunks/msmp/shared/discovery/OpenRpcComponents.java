package space.chunks.msmp.shared.discovery;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class OpenRpcComponents {
    private final Map<String, SchemaDescriptor> schemas;

    public OpenRpcComponents(Map<String, SchemaDescriptor> schemas) {
        this.schemas = schemas == null
            ? Collections.<String, SchemaDescriptor>emptyMap()
            : Collections.unmodifiableMap(new LinkedHashMap<String, SchemaDescriptor>(schemas));
    }

    public Map<String, SchemaDescriptor> getSchemas() {
        return schemas;
    }
}
