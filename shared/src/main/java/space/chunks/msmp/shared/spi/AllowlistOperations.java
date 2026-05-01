package space.chunks.msmp.shared.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.Player;

public interface AllowlistOperations {
    CompletableFuture<List<Player>> getAllowlist();

    CompletableFuture<List<Player>> setAllowlist(List<Player> players);

    CompletableFuture<List<Player>> addToAllowlist(List<Player> players);

    CompletableFuture<List<Player>> removeFromAllowlist(List<Player> players);

    CompletableFuture<List<Player>> clearAllowlist();
}
