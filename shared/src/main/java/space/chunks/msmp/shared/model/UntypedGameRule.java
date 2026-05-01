package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public final class UntypedGameRule {
    private final String key;
    private final JsonNode value;

    @JsonCreator
    public UntypedGameRule(@JsonProperty("key") String key, @JsonProperty("value") JsonNode value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public JsonNode getValue() {
        return value;
    }
}
