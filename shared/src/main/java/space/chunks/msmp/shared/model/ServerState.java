package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ServerState {
    private final List<Player> players;
    private final boolean started;
    private final Version version;

    @JsonCreator
    public ServerState(
        @JsonProperty("players") List<Player> players,
        @JsonProperty("started") boolean started,
        @JsonProperty("version") Version version
    ) {
        this.players = players == null
            ? Collections.<Player>emptyList()
            : Collections.unmodifiableList(new ArrayList<Player>(players));
        this.started = started;
        this.version = version;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isStarted() {
        return started;
    }

    public Version getVersion() {
        return version;
    }
}
