package space.chunks.msmp.bungeecord;

import space.chunks.msmp.shared.protocol.MinecraftProtocolCatalog;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;

final class BungeeManagementCapabilities {
    private BungeeManagementCapabilities() {
    }

    static PlatformCapabilities create() {
        return PlatformCapabilities.builder()
            .method(MinecraftProtocolCatalog.RPC_DISCOVER.name())
            .group("players", "kick")
            .minecraft("server/status")
            .minecraft("server/stop")
            .minecraft("server/system_message")
            .notifications("server", "started", "stopping", "activity", "status")
            .notifications("players", "joined", "left")
            .build();
    }
}
