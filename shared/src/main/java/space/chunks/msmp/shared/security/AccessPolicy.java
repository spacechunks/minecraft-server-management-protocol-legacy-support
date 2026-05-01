package space.chunks.msmp.shared.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import space.chunks.msmp.shared.config.ManagementProtocolSettings;
import space.chunks.msmp.shared.api.RequestContext;

public final class AccessPolicy {
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String ORIGIN_HEADER = "origin";
    public static final String WEB_SOCKET_PROTOCOL_HEADER = "sec-websocket-protocol";
    public static final String WEB_SOCKET_PROTOCOL = "minecraft-v1";
    public static final String WEB_SOCKET_PROTOCOL_PREFIX = WEB_SOCKET_PROTOCOL + ",";

    private final ManagementProtocolSettings settings;

    public AccessPolicy(ManagementProtocolSettings settings) {
        this.settings = settings;
    }

    public AccessDecision authorize(RequestContext context) {
        if (settings.getSecret() == null || settings.getSecret().isEmpty()) {
            return AccessDecision.denied("management server secret is not configured");
        }

        String providedSecret = bearerToken(context.headers());
        if (providedSecret != null) {
            return isValidSecret(providedSecret)
                ? AccessDecision.allowed()
                : AccessDecision.denied("invalid management server secret");
        }

        providedSecret = webSocketProtocolSecret(context.headers());
        if (providedSecret == null) {
            return AccessDecision.denied("missing management server secret");
        }

        AccessDecision originDecision = authorizeOrigin(context);
        if (!originDecision.isAllowed()) {
            return originDecision;
        }

        if (!isValidSecret(providedSecret)) {
            return AccessDecision.denied("invalid management server secret");
        }

        return AccessDecision.allowed();
    }

    private AccessDecision authorizeOrigin(RequestContext context) {
        List<String> allowedOrigins = settings.getAllowedOrigins();
        if (allowedOrigins.isEmpty()) {
            return AccessDecision.denied("no allowed origins are configured");
        }

        String origin = context.origin();
        if (origin == null) {
            origin = header(context.headers(), ORIGIN_HEADER);
        }

        if (origin == null || !allowedOrigins.contains(origin)) {
            return AccessDecision.denied("origin is not allowed");
        }

        return AccessDecision.allowed();
    }

    private static String bearerToken(Map<String, String> headers) {
        String authorization = header(headers, AUTHORIZATION_HEADER);
        if (authorization == null) {
            return null;
        }

        String prefix = "Bearer ";
        return authorization.startsWith(prefix) ? authorization.substring(prefix.length()) : null;
    }

    private boolean isValidSecret(String providedSecret) {
        byte[] configured = settings.getSecret().getBytes(StandardCharsets.UTF_8);
        byte[] provided = providedSecret.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(configured, provided);
    }

    private static String webSocketProtocolSecret(Map<String, String> headers) {
        String protocolHeader = header(headers, WEB_SOCKET_PROTOCOL_HEADER);
        if (protocolHeader == null) {
            return null;
        }

        if (protocolHeader.startsWith(WEB_SOCKET_PROTOCOL_PREFIX)) {
            return protocolHeader.substring(WEB_SOCKET_PROTOCOL_PREFIX.length()).trim();
        }
        return null;
    }

    private static String header(Map<String, String> headers, String name) {
        if (headers == null || name == null) {
            return null;
        }

        String direct = headers.get(name);
        if (direct != null) {
            return direct;
        }

        String lowerName = name.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey() != null && lowerName.equals(entry.getKey().toLowerCase(Locale.ROOT))) {
                return entry.getValue();
            }
        }

        return null;
    }
}
