package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class KickPlayer {
    private final Player player;
    private final Message message;

    @JsonCreator
    public KickPlayer(@JsonProperty("player") Player player, @JsonProperty("message") Message message) {
        this.player = player;
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public Message getMessage() {
        return message;
    }
}
