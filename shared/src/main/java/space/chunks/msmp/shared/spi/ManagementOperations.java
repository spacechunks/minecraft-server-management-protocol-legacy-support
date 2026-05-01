package space.chunks.msmp.shared.spi;

import space.chunks.msmp.shared.protocol.PlatformCapabilities;

public interface ManagementOperations {
    PlatformCapabilities capabilities();

    AllowlistOperations allowlist();

    BanOperations bans();

    IpBanOperations ipBans();

    PlayerOperations players();

    OperatorOperations operators();

    ServerOperations server();

    ServerSettingsOperations serverSettings();

    GameRuleOperations gameRules();
}
