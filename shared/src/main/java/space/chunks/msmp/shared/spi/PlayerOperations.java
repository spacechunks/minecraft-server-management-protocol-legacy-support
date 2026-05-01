package space.chunks.msmp.shared.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.KickPlayer;
import space.chunks.msmp.shared.model.Player;

public interface PlayerOperations {
    CompletableFuture<List<Player>> getPlayers();

    CompletableFuture<List<Player>> kickPlayers(List<KickPlayer> players);
}
