package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.api.RequestContext;
import space.chunks.msmp.shared.jsonrpc.JsonRpcError;
import space.chunks.msmp.shared.jsonrpc.JsonRpcErrorCode;
import space.chunks.msmp.shared.jsonrpc.JsonRpcMessageCodec;
import space.chunks.msmp.shared.jsonrpc.JsonRpcRequest;
import space.chunks.msmp.shared.jsonrpc.JsonRpcResponse;

public final class JsonRpcFrameHandler {
    private final JsonRpcMessageCodec codec;
    private final JsonRpcDispatcher dispatcher;

    public JsonRpcFrameHandler(JsonRpcMessageCodec codec, JsonRpcDispatcher dispatcher) {
        this.codec = codec;
        this.dispatcher = dispatcher;
    }

    public CompletableFuture<String> handle(String message, RequestContext context) {
        JsonNode root;
        try {
            root = codec.parse(message);
        } catch (JsonProcessingException exception) {
            return completed(error(null, JsonRpcError.of(JsonRpcErrorCode.PARSE_ERROR, JsonRpcErrorCode.PARSE_ERROR.defaultMessage(), TextNode.valueOf(exception.getOriginalMessage()))));
        }

        if (root.isObject()) {
            return handleSingle(root, context);
        }

        if (root.isArray()) {
            return handleBatch((ArrayNode) root, context);
        }

        return completed(error(null, JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST)));
    }

    private CompletableFuture<String> handleSingle(JsonNode node, RequestContext context) {
        JsonRpcRequest request;
        try {
            request = codec.request(node);
        } catch (JsonProcessingException exception) {
            return completed(error(null, JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST, JsonRpcErrorCode.INVALID_REQUEST.defaultMessage(), TextNode.valueOf(exception.getOriginalMessage()))));
        }

        return dispatcher.dispatch(request, context).thenApply(this::writeNullableResponse);
    }

    private CompletableFuture<String> handleBatch(ArrayNode batch, RequestContext context) {
        if (batch.size() == 0) {
            return completed(error(null, JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST)));
        }

        CompletableFuture<ArrayNode> combined = CompletableFuture.completedFuture(batch.arrayNode());
        for (JsonNode entry : batch) {
            combined = combined.thenCombine(handleBatchEntry(entry, context), (responses, response) -> {
                if (response != null) {
                    responses.add(response);
                }
                return responses;
            });
        }

        return combined.thenApply(responses -> {
            if (responses.size() == 0) {
                return null;
            }
            try {
                return codec.write(responses);
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException(exception);
            }
        });
    }

    private CompletableFuture<JsonNode> handleBatchEntry(JsonNode entry, RequestContext context) {
        if (!entry.isObject()) {
            return CompletableFuture.completedFuture(codec.value(JsonRpcResponse.error(null, JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST))));
        }

        JsonRpcRequest request;
        try {
            request = codec.request(entry);
        } catch (JsonProcessingException exception) {
            return CompletableFuture.completedFuture(codec.value(JsonRpcResponse.error(null, JsonRpcError.of(JsonRpcErrorCode.INVALID_REQUEST, JsonRpcErrorCode.INVALID_REQUEST.defaultMessage(), TextNode.valueOf(exception.getOriginalMessage())))));
        }

        return dispatcher.dispatch(request, context).thenApply(response -> response == null ? null : codec.value(response));
    }

    private String writeNullableResponse(JsonRpcResponse response) {
        if (response == null) {
            return null;
        }

        try {
            return codec.write(response);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String error(JsonNode id, JsonRpcError error) {
        try {
            return codec.write(JsonRpcResponse.error(id, error));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static <T> CompletableFuture<T> completed(T value) {
        return CompletableFuture.completedFuture(value);
    }
}
