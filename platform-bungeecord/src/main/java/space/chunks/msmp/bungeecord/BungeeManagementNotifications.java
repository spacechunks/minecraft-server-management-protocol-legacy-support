package space.chunks.msmp.bungeecord;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import space.chunks.msmp.shared.ManagementNotifications;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.api.NotificationPublisher;
import space.chunks.msmp.shared.model.ServerState;

final class BungeeManagementNotifications implements Listener {
    private final Plugin plugin;
    private final BungeeManagementOperations operations;
    private final ManagementNotifications notifications;
    private ScheduledTask heartbeatTask;

    BungeeManagementNotifications(Plugin plugin, PlatformBridge platform, BungeeManagementOperations operations, NotificationPublisher publisher) {
        this.plugin = plugin;
        this.operations = operations;
        this.notifications = new ManagementNotifications(platform, publisher);
    }

    void start() {
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
        notifications.publish("server/started");
        heartbeatTask = plugin.getProxy().getScheduler().schedule(plugin, this::statusHeartbeat, 30L, 30L, TimeUnit.SECONDS);
    }

    void stop() {
        notifications.publish("server/stopping");
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
            heartbeatTask = null;
        }
    }

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        notifications.publish("players/joined", operations.toModel(event.getPlayer()));
        notifications.publish("server/activity");
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        notifications.publish("players/left", operations.toModel(event.getPlayer()));
    }

    private void statusHeartbeat() {
        notifications.publish("server/status", new ServerState(operations.onlinePlayers(), true, operations.version()));
    }
}
