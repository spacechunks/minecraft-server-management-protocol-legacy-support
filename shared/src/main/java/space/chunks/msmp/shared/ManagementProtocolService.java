package space.chunks.msmp.shared;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import space.chunks.msmp.shared.config.ManagementProtocolSettings;
import space.chunks.msmp.shared.jsonrpc.JsonRpcMessageCodec;
import space.chunks.msmp.shared.json.ObjectMapperFactory;
import space.chunks.msmp.shared.protocol.ProtocolVersion;
import space.chunks.msmp.shared.jsonrpc.JsonRpcDispatcher;
import space.chunks.msmp.shared.jsonrpc.JsonRpcFrameHandler;
import space.chunks.msmp.shared.api.ManagementMethodBinder;
import space.chunks.msmp.shared.api.ManagementNotificationPublisher;
import space.chunks.msmp.shared.api.MethodRegistry;
import space.chunks.msmp.shared.api.NotificationPublisher;
import space.chunks.msmp.shared.security.SecretGenerator;
import space.chunks.msmp.shared.transport.ManagementWebSocketServer;

public final class ManagementProtocolService {
    private final PlatformBridge platform;
    private final ManagementProtocolSettings settings;
    private final MethodRegistry methods;
    private final ManagementNotificationPublisher notifications;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ManagementWebSocketServer server;

    public ManagementProtocolService(PlatformBridge platform, ManagementProtocolSettings settings) {
        this(platform, settings, new ManagementMethodBinder().bindDiscoveryOnly(ProtocolVersion.V3_0_0));
    }

    public ManagementProtocolService(PlatformBridge platform, ManagementProtocolSettings settings, MethodRegistry methods) {
        this(platform, settings, methods, new ManagementNotificationPublisher());
    }

    public ManagementProtocolService(
        PlatformBridge platform,
        ManagementProtocolSettings settings,
        MethodRegistry methods,
        ManagementNotificationPublisher notifications
    ) {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.methods = Objects.requireNonNull(methods, "methods");
        this.notifications = Objects.requireNonNull(notifications, "notifications");
    }

    public void start() {
        if (!settings.isEnabled()) {
            platform.info("Minecraft Server Management Protocol compatibility service is disabled.");
            return;
        }

        validateSettings();

        if (running.compareAndSet(false, true)) {
            try {
                JsonRpcMessageCodec codec = new JsonRpcMessageCodec(ObjectMapperFactory.create());
                JsonRpcDispatcher dispatcher = new JsonRpcDispatcher(methods);
                JsonRpcFrameHandler frameHandler = new JsonRpcFrameHandler(codec, dispatcher);
                server = new ManagementWebSocketServer(platform, settings, frameHandler);
                notifications.attach(server);
                server.start();
                platform.info("Minecraft Server Management Protocol compatibility service started with "
                    + methods.size() + " registered methods.");
            } catch (RuntimeException exception) {
                running.set(false);
                throw exception;
            }
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (server != null) {
                try {
                    server.stop(5000);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    platform.warn("Interrupted while stopping the management WebSocket server.");
                } finally {
                    notifications.detach();
                    server = null;
                }
            }
            platform.info("Minecraft Server Management Protocol compatibility service stopped.");
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public MethodRegistry methods() {
        return methods;
    }

    public NotificationPublisher notifications() {
        return notifications;
    }

    private void validateSettings() {
        String secret = settings.getSecret();
        if (!SecretGenerator.isValid(secret)) {
            throw new IllegalStateException("Invalid management server secret, must be 40 alphanumeric characters");
        }
    }
}
