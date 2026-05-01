package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonRpcError {
    private final int code;
    private final String message;
    private final JsonNode data;

    public JsonRpcError(int code, String message, JsonNode data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static JsonRpcError of(JsonRpcErrorCode code) {
        return new JsonRpcError(code.code(), code.defaultMessage(), null);
    }

    public static JsonRpcError of(JsonRpcErrorCode code, String message, JsonNode data) {
        return new JsonRpcError(code.code(), message, data);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JsonNode getData() {
        return data;
    }
}
