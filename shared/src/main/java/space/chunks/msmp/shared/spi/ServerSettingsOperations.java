package space.chunks.msmp.shared.spi;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.protocol.ServerSettingKey;

public interface ServerSettingsOperations {
    CompletableFuture<JsonNode> getSetting(ServerSettingKey key);

    CompletableFuture<JsonNode> setSetting(ServerSettingKey key, JsonNode value);
}
