package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Player {
    private final String name;
    private final UUID id;

    @JsonCreator
    public Player(@JsonProperty("name") String name, @JsonProperty("id") UUID id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}
