package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UserBan {
    private final String reason;
    private final Instant expires;
    private final String source;
    private final Player player;

    @JsonCreator
    public UserBan(
        @JsonProperty("reason") String reason,
        @JsonProperty("expires") Instant expires,
        @JsonProperty("source") String source,
        @JsonProperty("player") Player player
    ) {
        this.reason = reason;
        this.expires = expires;
        this.source = source;
        this.player = player;
    }

    public String getReason() {
        return reason;
    }

    public Instant getExpires() {
        return expires;
    }

    public String getSource() {
        return source;
    }

    public Player getPlayer() {
        return player;
    }
}
