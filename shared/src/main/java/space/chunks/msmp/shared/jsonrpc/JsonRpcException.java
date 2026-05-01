package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.databind.JsonNode;

public final class JsonRpcException extends RuntimeException {
    private final JsonRpcErrorCode code;
    private final JsonNode data;

    public JsonRpcException(JsonRpcErrorCode code, String message) {
        this(code, message, null);
    }

    public JsonRpcException(JsonRpcErrorCode code, String message, JsonNode data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public JsonRpcErrorCode code() {
        return code;
    }

    public JsonNode data() {
        return data;
    }

    public JsonRpcError toError() {
        JsonNode errorData = data == null
            ? com.fasterxml.jackson.databind.node.TextNode.valueOf(getMessage())
            : data;
        return JsonRpcError.of(code, code.defaultMessage(), errorData);
    }
}
