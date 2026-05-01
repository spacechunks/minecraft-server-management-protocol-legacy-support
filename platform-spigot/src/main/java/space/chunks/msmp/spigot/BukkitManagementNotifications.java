package space.chunks.msmp.spigot;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import space.chunks.msmp.shared.ManagementNotifications;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.model.ServerState;
import space.chunks.msmp.shared.model.Version;
import space.chunks.msmp.shared.protocol.MinecraftJavaProtocolVersions;
import space.chunks.msmp.shared.api.NotificationPublisher;

public final class BukkitManagementNotifications implements Listener {
    private static final long ACTIVITY_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final long STATUS_HEARTBEAT_TICKS = 20L * 30L;

    private final Plugin plugin;
    private final ManagementNotifications notifications;
    private final BukkitPlayerLookup players;

    private long lastActivityNotification;
    private int heartbeatTaskId = -1;

    public BukkitManagementNotifications(Plugin plugin, PlatformBridge platform, NotificationPublisher publisher, BukkitPlayerLookup players) {
        this.plugin = plugin;
        this.notifications = new ManagementNotifications(platform, publisher);
        this.players = players;
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        publish("server/started");
        heartbeatTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::statusHeartbeat, STATUS_HEARTBEAT_TICKS, STATUS_HEARTBEAT_TICKS);
    }

    public void stop() {
        publish("server/stopping");
        if (heartbeatTaskId != -1) {
            Bukkit.getScheduler().cancelTask(heartbeatTaskId);
            heartbeatTaskId = -1;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        publish("players/joined", players.toModel(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        publish("players/left", players.toModel(event.getPlayer()));
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        long now = System.currentTimeMillis();
        if (now - lastActivityNotification >= ACTIVITY_INTERVAL_MILLIS) {
            lastActivityNotification = now;
            publish("server/activity");
        }
    }

    public void statusHeartbeat() {
        publish("server/status", new ServerState(players.online(), true, version()));
    }

    public void publish(String path) {
        notifications.publish(path);
    }

    public void publish(String path, Object payload) {
        notifications.publish(path, payload);
    }

    private static Version version() {
        String version = Bukkit.getBukkitVersion();
        int protocol = MinecraftJavaProtocolVersions.protocolForVersionText(version);
        return new Version(protocol, version);
    }
}
