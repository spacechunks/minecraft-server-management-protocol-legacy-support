package space.chunks.msmp.shared.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.jsonrpc.JsonRpcRequest;

public interface MethodHandler {
    CompletableFuture<JsonNode> handle(JsonRpcRequest request, RequestContext context);
}
