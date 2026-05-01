package space.chunks.msmp.shared.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.discovery.MethodDescriptor;
import space.chunks.msmp.shared.discovery.OpenRpcDocument;
import space.chunks.msmp.shared.api.routes.ManagementMethodRoutes;
import space.chunks.msmp.shared.jsonrpc.JsonRpcParamReader;
import space.chunks.msmp.shared.jsonrpc.JsonRpcRequest;
import space.chunks.msmp.shared.json.ObjectMapperFactory;
import space.chunks.msmp.shared.protocol.MethodName;
import space.chunks.msmp.shared.protocol.MinecraftProtocolCatalog;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;
import space.chunks.msmp.shared.protocol.ProtocolVersion;
import space.chunks.msmp.shared.protocol.ServerSettingKey;
import space.chunks.msmp.shared.spi.ManagementOperations;

public final class ManagementMethodBinder {
    private final ObjectMapper mapper;
    private final JsonRpcParamReader params;

    public ManagementMethodBinder() {
        this(ObjectMapperFactory.create());
    }

    public ManagementMethodBinder(ObjectMapper mapper) {
        this.mapper = mapper;
        this.params = new JsonRpcParamReader(mapper);
    }

    public MethodRegistry bindDiscoveryOnly(ProtocolVersion protocolVersion) {
        MethodRegistry.Builder builder = MethodRegistry.builder();
        builder.add(MinecraftProtocolCatalog.RPC_DISCOVER, (request, context) ->
            CompletableFuture.completedFuture(mapper.valueToTree(OpenRpcDocument.minecraft(protocolVersion, PlatformCapabilities.discoveryOnly())))
        );
        return builder.build();
    }

    public MethodRegistry bind(ManagementOperations operations, ProtocolVersion protocolVersion) {
        MethodRegistry.Builder builder = MethodRegistry.builder();
        PlatformCapabilities capabilities = operations.capabilities();
        Map<MethodName, MethodHandler> handlers = ManagementMethodRoutes.bindAll(operations, params, mapper);

        builder.add(MinecraftProtocolCatalog.RPC_DISCOVER, (request, context) ->
            CompletableFuture.completedFuture(mapper.valueToTree(OpenRpcDocument.minecraft(protocolVersion, capabilities)))
        );

        for (MethodDescriptor descriptor : MinecraftProtocolCatalog.methods()) {
            if (MethodName.RPC_DISCOVER.equals(descriptor.name().value()) || !capabilities.supports(descriptor.name())) {
                continue;
            }
            builder.add(descriptor, handlerFor(descriptor, operations, handlers));
        }

        return builder.build();
    }

    private MethodHandler handlerFor(MethodDescriptor descriptor, ManagementOperations operations, Map<MethodName, MethodHandler> handlers) {
        MethodName method = descriptor.name();
        MethodHandler handler = handlers.get(method);
        if (handler != null) {
            return handler;
        }

        ServerSettingKey setting = serverSetting(method.value());
        if (setting != null) {
            if (method.value().endsWith("/set")) {
                return (request, context) -> operations.serverSettings().setSetting(setting, params.requiredNode(request, descriptor.parameter().getName()));
            }
            return noParams(request -> operations.serverSettings().getSetting(setting));
        }

        throw new IllegalArgumentException("No shared method binding for " + method.value());
    }

    private MethodHandler noParams(Invocation invocation) {
        return (request, context) -> {
            params.requireNoParams(request);
            return invocation.invoke(request).thenApply(mapper::valueToTree);
        };
    }

    private static ServerSettingKey serverSetting(String method) {
        String prefix = "minecraft:serversettings/";
        if (!method.startsWith(prefix)) {
            return null;
        }
        String key = method.substring(prefix.length());
        if (key.endsWith("/set")) {
            key = key.substring(0, key.length() - "/set".length());
        }
        for (ServerSettingKey setting : ServerSettingKey.values()) {
            if (setting.path().equals(key)) {
                return setting;
            }
        }
        return null;
    }

    private interface Invocation {
        CompletableFuture<?> invoke(JsonRpcRequest request);
    }
}
