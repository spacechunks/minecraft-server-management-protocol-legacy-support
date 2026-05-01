package space.chunks.msmp.shared.jsonrpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import space.chunks.msmp.shared.jsonrpc.JsonRpcErrorCode;
import space.chunks.msmp.shared.jsonrpc.JsonRpcException;
import space.chunks.msmp.shared.jsonrpc.JsonRpcRequest;

public final class JsonRpcParamReader {
    private final ObjectMapper mapper;

    public JsonRpcParamReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T required(JsonRpcRequest request, String parameterName, Class<T> type) {
        JsonNode value = requiredNode(request, parameterName);
        try {
            return mapper.treeToValue(value, type);
        } catch (IOException exception) {
            throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, exception.getMessage());
        }
    }

    public <T> List<T> requiredList(JsonRpcRequest request, String parameterName, Class<T> elementType) {
        JsonNode value = requiredNode(request, parameterName);
        try {
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
            return mapper.readerFor(type).readValue(value);
        } catch (IOException exception) {
            throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, exception.getMessage());
        }
    }

    public JsonNode requiredNode(JsonRpcRequest request, String parameterName) {
        JsonNode params = request.getParams();
        if (params == null || params.isNull()) {
            throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, "Expected params as array or named");
        }

        if (params.isObject()) {
            JsonNode value = params.get(parameterName);
            if (value == null) {
                throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, "Params passed by-name, but expected param [" + parameterName + "] does not exist");
            }
            return value;
        }

        if (params.isArray()) {
            if (params.size() != 1) {
                throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, "Expected exactly one element in the params array");
            }
            return params.get(0);
        }

        throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, "Expected params as array or named");
    }

    public void requireNoParams(JsonRpcRequest request) {
        JsonNode params = request.getParams();
        if (params == null || params.isNull()) {
            return;
        }

        if (!params.isArray() || params.size() != 0) {
            throw new JsonRpcException(JsonRpcErrorCode.INVALID_PARAMS, "Expected no params, or an empty array");
        }
    }
}
