package space.chunks.msmp.spigot.operations;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.chunks.msmp.spigot.BukkitManagementNotifications;
import space.chunks.msmp.spigot.BukkitPlayerLookup;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.spi.AllowlistOperations;

final class BukkitAllowlistOperations implements AllowlistOperations {
    private final BukkitServerThreadExecutor executor;
    private final BukkitPlayerLookup players;
    private final BukkitManagementNotifications notifications;

    BukkitAllowlistOperations(BukkitServerThreadExecutor executor, BukkitPlayerLookup players, BukkitManagementNotifications notifications) {
        this.executor = executor;
        this.players = players;
        this.notifications = notifications;
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> getAllowlist() {
        return executor.call(players::allowlist);
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> setAllowlist(List<space.chunks.msmp.shared.model.Player> players) {
        return executor.call(() -> {
            for (OfflinePlayer current : Bukkit.getWhitelistedPlayers()) {
                current.setWhitelisted(false);
                notifications.publish("allowlist/removed", this.players.toModel(current));
            }
            setWhitelisted(players, true);
            return this.players.allowlist();
        });
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> addToAllowlist(List<space.chunks.msmp.shared.model.Player> players) {
        return executor.call(() -> {
            setWhitelisted(players, true);
            return this.players.allowlist();
        });
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> removeFromAllowlist(List<space.chunks.msmp.shared.model.Player> players) {
        return executor.call(() -> {
            setWhitelisted(players, false);
            return this.players.allowlist();
        });
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> clearAllowlist() {
        return executor.call(() -> {
            for (OfflinePlayer current : Bukkit.getWhitelistedPlayers()) {
                current.setWhitelisted(false);
                notifications.publish("allowlist/removed", players.toModel(current));
            }
            return players.allowlist();
        });
    }

    private void setWhitelisted(List<space.chunks.msmp.shared.model.Player> players, boolean whitelisted) {
        for (space.chunks.msmp.shared.model.Player player : players) {
            this.players.offline(player).setWhitelisted(whitelisted);
            notifications.publish(whitelisted ? "allowlist/added" : "allowlist/removed", player);
        }
    }
}
