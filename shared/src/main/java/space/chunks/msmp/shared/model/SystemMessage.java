package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SystemMessage {
    private final List<Player> receivingPlayers;
    private final boolean overlay;
    private final Message message;

    @JsonCreator
    public SystemMessage(
        @JsonProperty("receivingPlayers") List<Player> receivingPlayers,
        @JsonProperty("overlay") boolean overlay,
        @JsonProperty("message") Message message
    ) {
        this.receivingPlayers = receivingPlayers == null
            ? null
            : Collections.unmodifiableList(new ArrayList<Player>(receivingPlayers));
        this.overlay = overlay;
        this.message = message;
    }

    public List<Player> getReceivingPlayers() {
        return receivingPlayers;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public Message getMessage() {
        return message;
    }
}
