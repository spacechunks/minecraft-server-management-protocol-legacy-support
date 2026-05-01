package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.api.MethodRegistry;
import space.chunks.msmp.shared.api.RequestContext;
import space.chunks.msmp.shared.jsonrpc.JsonRpcConstants;
import space.chunks.msmp.shared.jsonrpc.JsonRpcError;
import space.chunks.msmp.shared.jsonrpc.JsonRpcErrorCode;
import space.chunks.msmp.shared.jsonrpc.JsonRpcException;
import space.chunks.msmp.shared.jsonrpc.JsonRpcRequest;
import space.chunks.msmp.shared.jsonrpc.JsonRpcResponse;

public final class JsonRpcDispatcher {
    private final MethodRegistry methods;

    public JsonRpcDispatcher(MethodRegistry methods) {
        this.methods = Objects.requireNonNull(methods, "methods");
    }

    public CompletableFuture<JsonRpcResponse> dispatch(JsonRpcRequest request, RequestContext context) {
        JsonRpcError invalidRequest = validate(request);
        if (invalidRequest != null) {
            return completed(JsonRpcResponse.error(request == null ? null : request.getId(), invalidRequest));
        }

        return methods.find(request.getMethod())
            .map(method -> method.handler().handle(request, context)
                .thenApply(result -> request.isNotification() ? null : JsonRpcResponse.result(request.getId(), result))
                .exceptionally(error -> request.isNotification() ? null : JsonRpcResponse.error(request.getId(), toJsonRpcError(error))))
            .orElseGet(() -> request.isNotification() ? completed(null) : completed(JsonRpcResponse.error(
                request.getId(),
                JsonRpcError.of(JsonRpcErrorCode.METHOD_NOT_FOUND, JsonRpcErrorCode.METHOD_NOT_FOUND.defaultMessage(), TextNode.valueOf("Method not found: " + request.getMethod()))
            )));
    }

    private static JsonRpcError validate(JsonRpcRequest request) {
        if (request == null) {
            return JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST);
        }

        if (!JsonRpcConstants.VERSION.equals(request.getJsonrpc())) {
            return JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST, JsonRpcErrorCode.INVALID_REQUEST.defaultMessage(), TextNode.valueOf("jsonrpc must be 2.0"));
        }

        if (request.getMethod() == null || request.getMethod().trim().isEmpty()) {
            return JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST, JsonRpcErrorCode.INVALID_REQUEST.defaultMessage(), TextNode.valueOf("method is required"));
        }

        JsonNode id = request.getId();
        if (id != null && !id.isNull() && !id.isTextual() && !id.isNumber()) {
            return JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST, JsonRpcErrorCode.INVALID_REQUEST.defaultMessage(), TextNode.valueOf("Invalid request id - only String, Number and NULL supported"));
        }

        return null;
    }

    private static JsonRpcError toJsonRpcError(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null) {
            current = current.getCause();
        }

        if (current instanceof JsonRpcException) {
            return ((JsonRpcException) current).toError();
        }

        return JsonRpcError.of(JsonRpcErrorCode.INTERNAL_ERROR);
    }

    private static <T> CompletableFuture<T> completed(T value) {
        return CompletableFuture.completedFuture(value);
    }
}
