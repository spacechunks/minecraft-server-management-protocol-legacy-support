package space.chunks.msmp.shared.spi;

import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.ServerState;
import space.chunks.msmp.shared.model.SystemMessage;

public interface ServerOperations {
    CompletableFuture<ServerState> status();

    CompletableFuture<Boolean> save(boolean flush);

    CompletableFuture<Boolean> stop();

    CompletableFuture<Boolean> sendSystemMessage(SystemMessage message);
}
