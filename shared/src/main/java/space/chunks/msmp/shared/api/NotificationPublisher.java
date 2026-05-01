package space.chunks.msmp.shared.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.discovery.NotificationDescriptor;

public interface NotificationPublisher {
    CompletableFuture<Void> publish(NotificationDescriptor descriptor, JsonNode params);
}
