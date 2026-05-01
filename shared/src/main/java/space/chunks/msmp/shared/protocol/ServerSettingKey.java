package space.chunks.msmp.shared.protocol;

public enum ServerSettingKey {
    AUTOSAVE("autosave"),
    DIFFICULTY("difficulty"),
    ENFORCE_ALLOWLIST("enforce_allowlist"),
    USE_ALLOWLIST("use_allowlist"),
    MAX_PLAYERS("max_players"),
    PAUSE_WHEN_EMPTY_SECONDS("pause_when_empty_seconds"),
    PLAYER_IDLE_TIMEOUT("player_idle_timeout"),
    ALLOW_FLIGHT("allow_flight"),
    MOTD("motd"),
    SPAWN_PROTECTION_RADIUS("spawn_protection_radius"),
    FORCE_GAME_MODE("force_game_mode"),
    GAME_MODE("game_mode"),
    VIEW_DISTANCE("view_distance"),
    SIMULATION_DISTANCE("simulation_distance"),
    ACCEPT_TRANSFERS("accept_transfers"),
    STATUS_HEARTBEAT_INTERVAL("status_heartbeat_interval"),
    OPERATOR_USER_PERMISSION_LEVEL("operator_user_permission_level"),
    HIDE_ONLINE_PLAYERS("hide_online_players"),
    STATUS_REPLIES("status_replies"),
    ENTITY_BROADCAST_RANGE("entity_broadcast_range");

    private final String path;

    ServerSettingKey(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
