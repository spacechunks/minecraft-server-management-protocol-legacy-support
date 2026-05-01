package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonRpcMessageCodec {
    private final ObjectMapper mapper;

    public JsonRpcMessageCodec(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode parse(String message) throws JsonProcessingException {
        return mapper.readTree(message);
    }

    public JsonRpcRequest request(JsonNode node) throws JsonProcessingException {
        return mapper.treeToValue(node, JsonRpcRequest.class);
    }

    public JsonNode value(Object value) {
        return mapper.valueToTree(value);
    }

    public String write(JsonNode node) throws JsonProcessingException {
        return mapper.writeValueAsString(node);
    }

    public String write(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }
}
