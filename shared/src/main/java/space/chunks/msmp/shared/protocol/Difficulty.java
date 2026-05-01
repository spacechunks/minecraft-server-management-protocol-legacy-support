package space.chunks.msmp.shared.protocol;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Difficulty {
    PEACEFUL("peaceful"),
    EASY("easy"),
    NORMAL("normal"),
    HARD("hard");

    private final String wireName;

    Difficulty(String wireName) {
        this.wireName = wireName;
    }

    @JsonValue
    public String wireName() {
        return wireName;
    }
}
