package space.chunks.msmp.shared.discovery;

public enum EndpointGroup {
    ALLOWLIST("allowlist"),
    BANS("bans"),
    IP_BANS("ip_bans"),
    PLAYERS("players"),
    OPERATORS("operators"),
    SERVER("server"),
    SERVER_SETTINGS("serversettings"),
    GAMERULES("gamerules"),
    RPC("rpc"),
    NOTIFICATION_SERVER("notification/server"),
    NOTIFICATION_PLAYERS("notification/players"),
    NOTIFICATION_OPERATORS("notification/operators"),
    NOTIFICATION_ALLOWLIST("notification/allowlist"),
    NOTIFICATION_IP_BANS("notification/ip_bans"),
    NOTIFICATION_BANS("notification/bans"),
    NOTIFICATION_GAMERULES("notification/gamerules");

    private final String path;

    EndpointGroup(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
