package space.chunks.msmp.shared.security;

public final class AccessDecision {
    private static final AccessDecision ALLOWED = new AccessDecision(true, null);

    private final boolean allowed;
    private final String reason;

    private AccessDecision(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static AccessDecision allowed() {
        return ALLOWED;
    }

    public static AccessDecision denied(String reason) {
        return new AccessDecision(false, reason);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String reason() {
        return reason;
    }
}
