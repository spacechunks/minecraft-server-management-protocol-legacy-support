package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import space.chunks.msmp.shared.protocol.GameRuleType;

public final class TypedGameRule {
    private final GameRuleType type;
    private final String key;
    private final JsonNode value;

    @JsonCreator
    public TypedGameRule(
        @JsonProperty("type") GameRuleType type,
        @JsonProperty("key") String key,
        @JsonProperty("value") JsonNode value
    ) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public GameRuleType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public JsonNode getValue() {
        return value;
    }
}
