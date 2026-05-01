package space.chunks.msmp.shared.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import space.chunks.msmp.shared.discovery.EndpointDescriptor;
import space.chunks.msmp.shared.discovery.EndpointGroup;
import space.chunks.msmp.shared.discovery.MethodDescriptor;
import space.chunks.msmp.shared.discovery.NotificationDescriptor;
import space.chunks.msmp.shared.discovery.ParameterDescriptor;
import space.chunks.msmp.shared.discovery.ResultDescriptor;
import space.chunks.msmp.shared.discovery.SchemaCatalog;
import space.chunks.msmp.shared.discovery.SchemaDescriptor;
import space.chunks.msmp.shared.api.routes.ManagementMethodRoutes;

public final class MinecraftProtocolCatalog {
    public static final MethodDescriptor RPC_DISCOVER = method(
        MethodName.raw(MethodName.RPC_DISCOVER),
        EndpointGroup.RPC,
        "Discover supported API schema",
        null,
        result("result", SchemaCatalog.STRING),
        false,
        false,
        true
    );

    private static final List<MethodDescriptor> METHODS = createMethods();
    private static final List<NotificationDescriptor> NOTIFICATIONS = createNotifications();

    private MinecraftProtocolCatalog() {
    }

    public static List<MethodDescriptor> methods() {
        return METHODS;
    }

    public static List<NotificationDescriptor> notifications() {
        return NOTIFICATIONS;
    }

    public static NotificationDescriptor notificationByPath(String path) {
        MethodName name = MethodName.notification(path);
        for (NotificationDescriptor notification : NOTIFICATIONS) {
            if (notification.name().equals(name)) {
                return notification;
            }
        }
        throw new IllegalArgumentException("Unknown Minecraft notification: " + path);
    }

    public static List<EndpointDescriptor> discoverableEndpoints() {
        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();
        addDiscoverable(endpoints, METHODS);
        addDiscoverable(endpoints, NOTIFICATIONS);
        return Collections.unmodifiableList(endpoints);
    }

    private static void addDiscoverable(List<EndpointDescriptor> target, List<? extends EndpointDescriptor> source) {
        for (EndpointDescriptor descriptor : source) {
            if (descriptor.discoverable()) {
                target.add(descriptor);
            }
        }
    }

    private static List<MethodDescriptor> createMethods() {
        List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>();
        methods.add(RPC_DISCOVER);

        methods.addAll(ManagementMethodRoutes.descriptors());
        addServerSettings(methods);

        return Collections.unmodifiableList(methods);
    }

    private static void addServerSettings(List<MethodDescriptor> methods) {
        methods.add(settingGet("autosave", "Get whether automatic world saving is enabled on the server", "enabled", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("autosave", "Enable or disable automatic world saving on the server", "enable", SchemaCatalog.BOOLEAN, "enabled", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("difficulty", "Get the current difficulty level of the server", "difficulty", ref("difficulty")));
        methods.add(settingSet("difficulty", "Set the difficulty level of the server", "difficulty", ref("difficulty"), "difficulty", ref("difficulty")));
        methods.add(settingGet("enforce_allowlist", "Get whether allowlist enforcement is enabled (kicks players immediately when removed from allowlist)", "enforced", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("enforce_allowlist", "Enable or disable allowlist enforcement (when enabled, players are kicked immediately upon removal from allowlist)", "enforce", SchemaCatalog.BOOLEAN, "enforced", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("use_allowlist", "Get whether the allowlist is enabled on the server", "used", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("use_allowlist", "Enable or disable the allowlist on the server (controls whether only allowlisted players can join)", "use", SchemaCatalog.BOOLEAN, "used", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("max_players", "Get the maximum number of players allowed to connect to the server", "max", SchemaCatalog.INTEGER));
        methods.add(settingSet("max_players", "Set the maximum number of players allowed to connect to the server", "max", SchemaCatalog.INTEGER, "max", SchemaCatalog.INTEGER));
        methods.add(settingGet("pause_when_empty_seconds", "Get the number of seconds before the game is automatically paused when no players are online", "seconds", SchemaCatalog.INTEGER));
        methods.add(settingSet("pause_when_empty_seconds", "Set the number of seconds before the game is automatically paused when no players are online", "seconds", SchemaCatalog.INTEGER, "seconds", SchemaCatalog.INTEGER));
        methods.add(settingGet("player_idle_timeout", "Get the number of seconds before idle players are automatically kicked from the server", "seconds", SchemaCatalog.INTEGER));
        methods.add(settingSet("player_idle_timeout", "Set the number of seconds before idle players are automatically kicked from the server", "seconds", SchemaCatalog.INTEGER, "seconds", SchemaCatalog.INTEGER));
        methods.add(settingGet("allow_flight", "Get whether flight is allowed for players in Survival mode", "allowed", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("allow_flight", "Allow or disallow flight for players in Survival mode", "allow", SchemaCatalog.BOOLEAN, "allowed", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("motd", "Get the server's message of the day displayed to players", "message", SchemaCatalog.STRING));
        methods.add(settingSet("motd", "Set the server's message of the day displayed to players", "message", SchemaCatalog.STRING, "message", SchemaCatalog.STRING));
        methods.add(settingGet("spawn_protection_radius", "Get the spawn protection radius in blocks (only operators can edit within this area)", "radius", SchemaCatalog.INTEGER));
        methods.add(settingSet("spawn_protection_radius", "Set the spawn protection radius in blocks (only operators can edit within this area)", "radius", SchemaCatalog.INTEGER, "radius", SchemaCatalog.INTEGER));
        methods.add(settingGet("force_game_mode", "Get whether players are forced to use the server's default game mode", "forced", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("force_game_mode", "Enable or disable forcing players to use the server's default game mode", "force", SchemaCatalog.BOOLEAN, "forced", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("game_mode", "Get the server's default game mode", "mode", ref("game_type")));
        methods.add(settingSet("game_mode", "Set the server's default game mode", "mode", ref("game_type"), "mode", ref("game_type")));
        methods.add(settingGet("view_distance", "Get the server's view distance in chunks", "distance", SchemaCatalog.INTEGER));
        methods.add(settingSet("view_distance", "Set the server's view distance in chunks", "distance", SchemaCatalog.INTEGER, "distance", SchemaCatalog.INTEGER));
        methods.add(settingGet("simulation_distance", "Get the server's simulation distance in chunks", "distance", SchemaCatalog.INTEGER));
        methods.add(settingSet("simulation_distance", "Set the server's simulation distance in chunks", "distance", SchemaCatalog.INTEGER, "distance", SchemaCatalog.INTEGER));
        methods.add(settingGet("accept_transfers", "Get whether the server accepts player transfers from other servers", "accepted", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("accept_transfers", "Enable or disable accepting player transfers from other servers", "accept", SchemaCatalog.BOOLEAN, "accepted", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("status_heartbeat_interval", "Get the interval in seconds between server status heartbeats", "seconds", SchemaCatalog.INTEGER));
        methods.add(settingSet("status_heartbeat_interval", "Set the interval in seconds between server status heartbeats", "seconds", SchemaCatalog.INTEGER, "seconds", SchemaCatalog.INTEGER));
        methods.add(settingGet("operator_user_permission_level", "Get default operator permission level", "level", SchemaCatalog.INTEGER));
        methods.add(settingSet("operator_user_permission_level", "Set default operator permission level", "level", SchemaCatalog.INTEGER, "level", SchemaCatalog.INTEGER));
        methods.add(settingGet("hide_online_players", "Get whether the server hides online player information from status queries", "hidden", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("hide_online_players", "Enable or disable hiding online player information from status queries", "hide", SchemaCatalog.BOOLEAN, "hidden", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("status_replies", "Get whether the server responds to connection status requests", "enabled", SchemaCatalog.BOOLEAN));
        methods.add(settingSet("status_replies", "Enable or disable the server responding to connection status requests", "enable", SchemaCatalog.BOOLEAN, "enabled", SchemaCatalog.BOOLEAN));
        methods.add(settingGet("entity_broadcast_range", "Get the entity broadcast range as a percentage", "percentage_points", SchemaCatalog.INTEGER));
        methods.add(settingSet("entity_broadcast_range", "Set the entity broadcast range as a percentage", "percentage_points", SchemaCatalog.INTEGER, "percentage_points", SchemaCatalog.INTEGER));
    }

    private static List<NotificationDescriptor> createNotifications() {
        List<NotificationDescriptor> notifications = new ArrayList<NotificationDescriptor>();

        notifications.add(notification("server/started", EndpointGroup.NOTIFICATION_SERVER, "Server started", null));
        notifications.add(notification("server/stopping", EndpointGroup.NOTIFICATION_SERVER, "Server shutting down", null));
        notifications.add(notification("server/saving", EndpointGroup.NOTIFICATION_SERVER, "Server save started", null));
        notifications.add(notification("server/saved", EndpointGroup.NOTIFICATION_SERVER, "Server save completed", null));
        notifications.add(notification("server/activity", EndpointGroup.NOTIFICATION_SERVER, "Server activity occurred. Rate limited to 1 notification per 30 seconds", null));
        notifications.add(notification("players/joined", EndpointGroup.NOTIFICATION_PLAYERS, "Player joined", param("player", ref("player"))));
        notifications.add(notification("players/left", EndpointGroup.NOTIFICATION_PLAYERS, "Player left", param("player", ref("player"))));
        notifications.add(notification("operators/added", EndpointGroup.NOTIFICATION_OPERATORS, "Player was oped", param("player", ref("operator"))));
        notifications.add(notification("operators/removed", EndpointGroup.NOTIFICATION_OPERATORS, "Player was deoped", param("player", ref("operator"))));
        notifications.add(notification("allowlist/added", EndpointGroup.NOTIFICATION_ALLOWLIST, "Player was added to allowlist", param("player", ref("player"))));
        notifications.add(notification("allowlist/removed", EndpointGroup.NOTIFICATION_ALLOWLIST, "Player was removed from allowlist", param("player", ref("player"))));
        notifications.add(notification("ip_bans/added", EndpointGroup.NOTIFICATION_IP_BANS, "Ip was added to ip ban list", param("player", ref("ip_ban"))));
        notifications.add(notification("ip_bans/removed", EndpointGroup.NOTIFICATION_IP_BANS, "Ip was removed from ip ban list", param("player", SchemaCatalog.STRING)));
        notifications.add(notification("bans/added", EndpointGroup.NOTIFICATION_BANS, "Player was added to ban list", param("player", ref("user_ban"))));
        notifications.add(notification("bans/removed", EndpointGroup.NOTIFICATION_BANS, "Player was removed from ban list", param("player", ref("player"))));
        notifications.add(notification("gamerules/updated", EndpointGroup.NOTIFICATION_GAMERULES, "Gamerule was changed", param("gamerule", ref("typed_game_rule"))));
        notifications.add(notification("server/status", EndpointGroup.NOTIFICATION_SERVER, "Server status heartbeat, including before the server has spun up", param("status", ref("server_state")), true));

        return Collections.unmodifiableList(notifications);
    }

    private static MethodDescriptor settingGet(String key, String description, String resultName, SchemaDescriptor resultSchema) {
        return method("serversettings/" + key, EndpointGroup.SERVER_SETTINGS, description, null, result(resultName, resultSchema));
    }

    private static MethodDescriptor settingSet(
        String key,
        String description,
        String paramName,
        SchemaDescriptor paramSchema,
        String resultName,
        SchemaDescriptor resultSchema
    ) {
        return method("serversettings/" + key + "/set", EndpointGroup.SERVER_SETTINGS, description, param(paramName, paramSchema), result(resultName, resultSchema));
    }

    private static MethodDescriptor method(String path, EndpointGroup group, String description, ParameterDescriptor parameter, ResultDescriptor result) {
        return method(path, group, description, parameter, result, false);
    }

    private static MethodDescriptor method(
        String path,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        ResultDescriptor result,
        boolean availableBeforeServerStart
    ) {
        return method(MethodName.minecraft(path), group, description, parameter, result, true, true, availableBeforeServerStart);
    }

    private static MethodDescriptor method(
        MethodName name,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        ResultDescriptor result,
        boolean discoverable,
        boolean runOnMainThread,
        boolean availableBeforeServerStart
    ) {
        return new MethodDescriptor(name, group, description, parameter, result, discoverable, runOnMainThread, availableBeforeServerStart);
    }

    private static NotificationDescriptor notification(String path, EndpointGroup group, String description, ParameterDescriptor parameter) {
        return notification(path, group, description, parameter, false);
    }

    private static NotificationDescriptor notification(
        String path,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        boolean availableBeforeServerStart
    ) {
        return new NotificationDescriptor(MethodName.notification(path), group, description, parameter, true, availableBeforeServerStart);
    }

    private static ParameterDescriptor param(String name, SchemaDescriptor schema) {
        return new ParameterDescriptor(name, schema);
    }

    private static ResultDescriptor result(String name, SchemaDescriptor schema) {
        return new ResultDescriptor(name, schema);
    }

    private static SchemaDescriptor ref(String name) {
        return SchemaCatalog.ref(name);
    }

    private static SchemaDescriptor array(SchemaDescriptor items) {
        return SchemaCatalog.array(items);
    }
}
