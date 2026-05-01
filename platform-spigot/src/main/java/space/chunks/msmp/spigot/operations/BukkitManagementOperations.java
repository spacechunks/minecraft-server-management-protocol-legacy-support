package space.chunks.msmp.spigot.operations;

import org.bukkit.plugin.Plugin;
import space.chunks.msmp.spigot.BukkitManagementCapabilities;
import space.chunks.msmp.spigot.BukkitManagementNotifications;
import space.chunks.msmp.spigot.mapping.BukkitBanMapper;
import space.chunks.msmp.spigot.mapping.BukkitGameRuleMapper;
import space.chunks.msmp.spigot.BukkitPlayerLookup;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;
import space.chunks.msmp.shared.spi.AllowlistOperations;
import space.chunks.msmp.shared.spi.BanOperations;
import space.chunks.msmp.shared.spi.GameRuleOperations;
import space.chunks.msmp.shared.spi.IpBanOperations;
import space.chunks.msmp.shared.spi.ManagementOperations;
import space.chunks.msmp.shared.spi.OperatorOperations;
import space.chunks.msmp.shared.spi.PlayerOperations;
import space.chunks.msmp.shared.spi.ServerOperations;
import space.chunks.msmp.shared.spi.ServerSettingsOperations;

public final class BukkitManagementOperations implements ManagementOperations {
    private final PlatformCapabilities capabilities = BukkitManagementCapabilities.create();

    private final AllowlistOperations allowlist;
    private final BanOperations bans;
    private final IpBanOperations ipBans;
    private final PlayerOperations players;
    private final OperatorOperations operators;
    private final ServerOperations server;
    private final ServerSettingsOperations serverSettings;
    private final GameRuleOperations gameRules;

    public BukkitManagementOperations(Plugin plugin, BukkitManagementNotifications notifications) {
        BukkitServerThreadExecutor executor = new BukkitServerThreadExecutor(plugin);
        BukkitPlayerLookup players = new BukkitPlayerLookup();
        BukkitBanMapper banMapper = new BukkitBanMapper();
        BukkitGameRuleMapper gameRuleMapper = new BukkitGameRuleMapper();

        this.allowlist = new BukkitAllowlistOperations(executor, players, notifications);
        this.bans = new BukkitBanOperations(executor, players, banMapper, notifications);
        this.ipBans = new BukkitIpBanOperations(executor, players, banMapper, notifications);
        this.players = new BukkitPlayerOperations(executor, players);
        this.operators = new BukkitOperatorOperations(executor, players, notifications);
        this.server = new BukkitServerOperations(executor, players, notifications);
        this.serverSettings = new BukkitServerSettingsOperations(executor);
        this.gameRules = new BukkitGameRuleOperations(executor, gameRuleMapper, notifications);
    }

    @Override
    public PlatformCapabilities capabilities() {
        return capabilities;
    }

    @Override
    public AllowlistOperations allowlist() {
        return allowlist;
    }

    @Override
    public BanOperations bans() {
        return bans;
    }

    @Override
    public IpBanOperations ipBans() {
        return ipBans;
    }

    @Override
    public PlayerOperations players() {
        return players;
    }

    @Override
    public OperatorOperations operators() {
        return operators;
    }

    @Override
    public ServerOperations server() {
        return server;
    }

    @Override
    public ServerSettingsOperations serverSettings() {
        return serverSettings;
    }

    @Override
    public GameRuleOperations gameRules() {
        return gameRules;
    }
}
