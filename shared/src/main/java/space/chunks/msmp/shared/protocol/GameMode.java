package space.chunks.msmp.shared.protocol;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GameMode {
    CREATIVE("creative"),
    SURVIVAL("survival"),
    SPECTATOR("spectator"),
    ADVENTURE("adventure");

    private final String wireName;

    GameMode(String wireName) {
        this.wireName = wireName;
    }

    @JsonValue
    public String wireName() {
        return wireName;
    }
}
