package space.chunks.msmp.shared.protocol;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GameRuleType {
    INTEGER("integer"),
    BOOLEAN("boolean");

    private final String wireName;

    GameRuleType(String wireName) {
        this.wireName = wireName;
    }

    @JsonValue
    public String wireName() {
        return wireName;
    }
}
