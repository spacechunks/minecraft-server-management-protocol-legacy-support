package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Version {
    private final int protocol;
    private final String name;

    @JsonCreator
    public Version(@JsonProperty("protocol") int protocol, @JsonProperty("name") String name) {
        this.protocol = protocol;
        this.name = name;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getName() {
        return name;
    }
}
