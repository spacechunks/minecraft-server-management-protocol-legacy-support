package space.chunks.msmp.shared.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RequestContext {
    private final String remoteAddress;
    private final String origin;
    private final Map<String, String> headers;

    public RequestContext(String remoteAddress, String origin, Map<String, String> headers) {
        this.remoteAddress = remoteAddress;
        this.origin = origin;
        this.headers = headers == null
            ? Collections.<String, String>emptyMap()
            : Collections.unmodifiableMap(new LinkedHashMap<String, String>(headers));
    }

    public static RequestContext local() {
        return new RequestContext("local", null, Collections.<String, String>emptyMap());
    }

    public String remoteAddress() {
        return remoteAddress;
    }

    public String origin() {
        return origin;
    }

    public Map<String, String> headers() {
        return headers;
    }
}
