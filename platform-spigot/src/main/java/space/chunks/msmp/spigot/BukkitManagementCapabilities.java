package space.chunks.msmp.spigot;

import space.chunks.msmp.shared.protocol.MinecraftProtocolCatalog;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;

public final class BukkitManagementCapabilities {
    private BukkitManagementCapabilities() {
    }

    public static PlatformCapabilities create() {
        return PlatformCapabilities.builder()
            .method(MinecraftProtocolCatalog.RPC_DISCOVER.name())
            .group("allowlist", "set", "add", "remove", "clear")
            .group("bans", "set", "add", "remove", "clear")
            .group("ip_bans", "set", "add", "remove", "clear")
            .group("players", "kick")
            .group("operators", "set", "add", "remove", "clear")
            .minecraft("server/status")
            .minecraft("server/save")
            .minecraft("server/stop")
            .minecraft("server/system_message")
            .serverSetting("autosave", true)
            .serverSetting("difficulty", true)
            .serverSetting("use_allowlist", true)
            .serverSetting("player_idle_timeout", true)
            .serverSetting("allow_flight", false)
            .serverSetting("motd", false)
            .serverSetting("spawn_protection_radius", true)
            .serverSetting("game_mode", true)
            .serverSetting("view_distance", false)
            .minecraft("gamerules")
            .minecraft("gamerules/update")
            .notifications("server", "started", "stopping", "saving", "saved", "activity", "status")
            .notifications("players", "joined", "left")
            .notifications("operators", "added", "removed")
            .notifications("allowlist", "added", "removed")
            .notifications("ip_bans", "added", "removed")
            .notifications("bans", "added", "removed")
            .notification("gamerules/updated")
            .build();
    }
}
