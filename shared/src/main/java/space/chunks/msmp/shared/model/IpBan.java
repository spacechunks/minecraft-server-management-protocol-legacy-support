package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class IpBan {
    private final String reason;
    private final Instant expires;
    private final String ip;
    private final String source;

    @JsonCreator
    public IpBan(
        @JsonProperty("reason") String reason,
        @JsonProperty("expires") Instant expires,
        @JsonProperty("ip") String ip,
        @JsonProperty("source") String source
    ) {
        this.reason = reason;
        this.expires = expires;
        this.ip = ip;
        this.source = source;
    }

    public String getReason() {
        return reason;
    }

    public Instant getExpires() {
        return expires;
    }

    public String getIp() {
        return ip;
    }

    public String getSource() {
        return source;
    }
}
