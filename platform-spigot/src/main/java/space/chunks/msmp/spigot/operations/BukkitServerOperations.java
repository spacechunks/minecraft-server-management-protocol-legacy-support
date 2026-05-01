package space.chunks.msmp.spigot.operations;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import space.chunks.msmp.spigot.BukkitManagementNotifications;
import space.chunks.msmp.shared.text.PlainTextMessageFormatter;
import space.chunks.msmp.spigot.BukkitPlayerLookup;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.model.ServerState;
import space.chunks.msmp.shared.model.SystemMessage;
import space.chunks.msmp.shared.model.Version;
import space.chunks.msmp.shared.protocol.MinecraftJavaProtocolVersions;
import space.chunks.msmp.shared.spi.ServerOperations;

final class BukkitServerOperations implements ServerOperations {
    private final BukkitServerThreadExecutor executor;
    private final BukkitPlayerLookup players;
    private final BukkitManagementNotifications notifications;

    BukkitServerOperations(BukkitServerThreadExecutor executor, BukkitPlayerLookup players, BukkitManagementNotifications notifications) {
        this.executor = executor;
        this.players = players;
        this.notifications = notifications;
    }

    @Override
    public CompletableFuture<ServerState> status() {
        return executor.call(() -> new ServerState(players.online(), true, version()));
    }

    @Override
    public CompletableFuture<Boolean> save(boolean flush) {
        return executor.call(() -> {
            notifications.publish("server/saving");
            Bukkit.savePlayers();
            for (World world : Bukkit.getWorlds()) {
                world.save();
            }
            notifications.publish("server/saved");
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> stop() {
        return executor.call(() -> {
            notifications.publish("server/stopping");
            Bukkit.shutdown();
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> sendSystemMessage(SystemMessage message) {
        return executor.call(() -> {
            String text = PlainTextMessageFormatter.format(message.getMessage());
            if (message.getReceivingPlayers() == null) {
                Bukkit.broadcastMessage(text);
                return true;
            }
            if (message.getReceivingPlayers().isEmpty()) {
                return false;
            }
            for (space.chunks.msmp.shared.model.Player target : message.getReceivingPlayers()) {
                Player player = players.online(target);
                if (player != null) {
                    player.sendMessage(text);
                }
            }
            return true;
        });
    }

    private static Version version() {
        String version = Bukkit.getBukkitVersion();
        int protocol = MinecraftJavaProtocolVersions.protocolForVersionText(version);
        return new Version(protocol, version);
    }
}
