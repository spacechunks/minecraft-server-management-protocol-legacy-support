package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

public final class JsonRpcResponse {
    private final String jsonrpc;
    private final JsonNode id;
    private final JsonNode result;
    private final JsonRpcError error;

    private JsonRpcResponse(JsonNode id, JsonNode result, JsonRpcError error) {
        this.jsonrpc = JsonRpcConstants.VERSION;
        this.id = id;
        this.result = result;
        this.error = error;
    }

    public static JsonRpcResponse result(JsonNode id, JsonNode result) {
        return new JsonRpcResponse(id, result, null);
    }

    public static JsonRpcResponse error(JsonNode id, JsonRpcError error) {
        return new JsonRpcResponse(id, null, error);
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public JsonNode getId() {
        return id;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public JsonNode getResult() {
        return result;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public JsonRpcError getError() {
        return error;
    }
}
