package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonRpcRequest {
    private final String jsonrpc;
    private final String method;
    private final JsonNode params;
    private final JsonNode id;

    @JsonCreator
    public JsonRpcRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("method") String method,
        @JsonProperty("params") JsonNode params,
        @JsonProperty("id") JsonNode id
    ) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public JsonNode getParams() {
        return params;
    }

    public JsonNode getId() {
        return id;
    }

    public boolean isNotification() {
        return id == null;
    }
}
