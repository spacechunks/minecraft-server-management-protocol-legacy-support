package space.chunks.msmp.shared.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import space.chunks.msmp.shared.protocol.MinecraftProtocolCatalog;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;
import space.chunks.msmp.shared.protocol.ProtocolVersion;

public final class OpenRpcDocument {
    public static final String OPENRPC_VERSION = "1.3.2";
    public static final String TITLE = "Minecraft Server JSON-RPC";

    private final String openrpc;
    private final OpenRpcInfo info;
    private final List<OpenRpcMethod> methods;
    private final OpenRpcComponents components;

    public OpenRpcDocument(String openrpc, OpenRpcInfo info, List<OpenRpcMethod> methods, OpenRpcComponents components) {
        this.openrpc = openrpc;
        this.info = info;
        this.methods = methods == null
            ? Collections.<OpenRpcMethod>emptyList()
            : Collections.unmodifiableList(new ArrayList<OpenRpcMethod>(methods));
        this.components = components;
    }

    public static OpenRpcDocument minecraft(ProtocolVersion protocolVersion) {
        return minecraft(protocolVersion, null);
    }

    public static OpenRpcDocument minecraft(ProtocolVersion protocolVersion, PlatformCapabilities capabilities) {
        List<OpenRpcMethod> methods = new ArrayList<OpenRpcMethod>();
        for (EndpointDescriptor endpoint : MinecraftProtocolCatalog.discoverableEndpoints()) {
            if (capabilities == null || capabilities.supports(endpoint.name())) {
                methods.add(new OpenRpcMethod(endpoint));
            }
        }

        return new OpenRpcDocument(
            OPENRPC_VERSION,
            new OpenRpcInfo(TITLE, protocolVersion.wireName()),
            methods,
            new OpenRpcComponents(SchemaCatalog.schemas())
        );
    }

    @JsonProperty("openrpc")
    public String getOpenrpc() {
        return openrpc;
    }

    public OpenRpcInfo getInfo() {
        return info;
    }

    public List<OpenRpcMethod> getMethods() {
        return methods;
    }

    public OpenRpcComponents getComponents() {
        return components;
    }
}
