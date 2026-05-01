package space.chunks.msmp.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.api.NotificationPublisher;
import space.chunks.msmp.shared.json.ObjectMapperFactory;
import space.chunks.msmp.shared.protocol.MinecraftProtocolCatalog;

public final class ManagementNotifications {
    private final PlatformBridge platform;
    private final NotificationPublisher publisher;
    private final ObjectMapper mapper = ObjectMapperFactory.create();

    public ManagementNotifications(PlatformBridge platform, NotificationPublisher publisher) {
        this.platform = platform;
        this.publisher = publisher;
    }

    public void publish(String path) {
        publisher.publish(MinecraftProtocolCatalog.notificationByPath(path), null)
            .exceptionally(error -> {
                platform.warn("Failed to publish management notification " + path + ": " + rootMessage(error));
                return null;
            });
    }

    public void publish(String path, Object payload) {
        publisher.publish(MinecraftProtocolCatalog.notificationByPath(path), mapper.valueToTree(payload))
            .exceptionally(error -> {
                platform.warn("Failed to publish management notification " + path + ": " + rootMessage(error));
                return null;
            });
    }

    private static String rootMessage(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }
}
