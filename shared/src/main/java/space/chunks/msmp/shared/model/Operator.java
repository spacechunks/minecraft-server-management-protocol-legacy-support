package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Operator {
    private final Integer permissionLevel;
    private final Boolean bypassesPlayerLimit;
    private final Player player;

    @JsonCreator
    public Operator(
        @JsonProperty("permissionLevel") Integer permissionLevel,
        @JsonProperty("bypassesPlayerLimit") Boolean bypassesPlayerLimit,
        @JsonProperty("player") Player player
    ) {
        this.permissionLevel = permissionLevel;
        this.bypassesPlayerLimit = bypassesPlayerLimit;
        this.player = player;
    }

    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    public Boolean getBypassesPlayerLimit() {
        return bypassesPlayerLimit;
    }

    public Player getPlayer() {
        return player;
    }
}
