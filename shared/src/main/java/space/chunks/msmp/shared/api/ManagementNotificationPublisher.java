package space.chunks.msmp.shared.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.discovery.NotificationDescriptor;
import space.chunks.msmp.shared.transport.ManagementWebSocketServer;

public final class ManagementNotificationPublisher implements NotificationPublisher {
    private volatile ManagementWebSocketServer server;

    public void attach(ManagementWebSocketServer server) {
        this.server = server;
    }

    public void detach() {
        this.server = null;
    }

    @Override
    public CompletableFuture<Void> publish(NotificationDescriptor descriptor, JsonNode params) {
        ManagementWebSocketServer current = server;
        if (current == null) {
            return CompletableFuture.completedFuture(null);
        }
        return current.publish(descriptor, params);
    }
}
