package space.chunks.msmp.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;
import space.chunks.msmp.shared.ManagementProtocolService;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.config.PropertiesFileSettings;
import space.chunks.msmp.shared.api.ManagementMethodBinder;
import space.chunks.msmp.shared.api.ManagementNotificationPublisher;
import space.chunks.msmp.shared.protocol.ProtocolVersion;

@Plugin(
    id = "msmp-legacy-support",
    name = "Minecraft Server Management Protocol Legacy Support",
    version = "0.1.0-SNAPSHOT",
    description = "Minecraft Server Management Protocol compatibility service for Velocity",
    authors = {"spacechunks"}
)
public final class MsmpVelocityPlugin implements PlatformBridge {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private ManagementProtocolService service;
    private VelocityManagementNotifications notifications;

    @Inject
    public MsmpVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        ManagementNotificationPublisher publisher = new ManagementNotificationPublisher();
        VelocityManagementOperations operations = new VelocityManagementOperations(proxy);
        notifications = new VelocityManagementNotifications(this, this, proxy, operations, publisher);
        service = new ManagementProtocolService(
            this,
            PropertiesFileSettings.loadOrCreate(dataDirectory.resolve("management-server.properties")),
            new ManagementMethodBinder().bind(operations, ProtocolVersion.V3_0_0),
            publisher
        );
        service.start();
        if (service.isRunning()) {
            proxy.getEventManager().register(this, notifications);
            notifications.start();
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (notifications != null) {
            notifications.stop();
        }
        if (service != null) {
            service.stop();
        }
    }

    @Override
    public String platformName() {
        return "velocity";
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }
}
