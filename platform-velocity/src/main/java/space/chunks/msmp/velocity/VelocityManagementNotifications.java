package space.chunks.msmp.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;
import space.chunks.msmp.shared.ManagementNotifications;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.api.NotificationPublisher;
import space.chunks.msmp.shared.model.ServerState;

final class VelocityManagementNotifications {
    private final Object plugin;
    private final ProxyServer proxy;
    private final VelocityManagementOperations operations;
    private final ManagementNotifications notifications;
    private ScheduledTask heartbeatTask;

    VelocityManagementNotifications(Object plugin, PlatformBridge platform, ProxyServer proxy, VelocityManagementOperations operations, NotificationPublisher publisher) {
        this.plugin = plugin;
        this.proxy = proxy;
        this.operations = operations;
        this.notifications = new ManagementNotifications(platform, publisher);
    }

    void start() {
        notifications.publish("server/started");
        heartbeatTask = proxy.getScheduler()
            .buildTask(plugin, this::statusHeartbeat)
            .repeat(30, TimeUnit.SECONDS)
            .schedule();
    }

    void stop() {
        notifications.publish("server/stopping");
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
            heartbeatTask = null;
        }
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        notifications.publish("players/joined", operations.toModel(event.getPlayer()));
        notifications.publish("server/activity");
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        notifications.publish("players/left", operations.toModel(event.getPlayer()));
    }

    private void statusHeartbeat() {
        notifications.publish("server/status", new ServerState(operations.onlinePlayers(), true, operations.version()));
    }
}
