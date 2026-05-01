package space.chunks.msmp.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import space.chunks.msmp.shared.ManagementProtocolService;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.config.PropertiesFileSettings;
import space.chunks.msmp.shared.api.ManagementMethodBinder;
import space.chunks.msmp.shared.api.ManagementNotificationPublisher;
import space.chunks.msmp.shared.protocol.ProtocolVersion;

public final class MsmpBungeePlugin extends Plugin implements PlatformBridge {
    private ManagementProtocolService service;
    private BungeeManagementNotifications notifications;

    @Override
    public void onEnable() {
        ManagementNotificationPublisher publisher = new ManagementNotificationPublisher();
        BungeeManagementOperations operations = new BungeeManagementOperations(getProxy());
        notifications = new BungeeManagementNotifications(this, this, operations, publisher);
        service = new ManagementProtocolService(
            this,
            PropertiesFileSettings.loadOrCreate(getDataFolder().toPath().resolve("management-server.properties")),
            new ManagementMethodBinder().bind(operations, ProtocolVersion.V3_0_0),
            publisher
        );
        service.start();
        if (service.isRunning()) {
            notifications.start();
        }
    }

    @Override
    public void onDisable() {
        if (notifications != null) {
            notifications.stop();
        }
        if (service != null) {
            service.stop();
        }
    }

    @Override
    public String platformName() {
        return "bungeecord";
    }

    @Override
    public void info(String message) {
        getLogger().info(message);
    }

    @Override
    public void warn(String message) {
        getLogger().warning(message);
    }
}
