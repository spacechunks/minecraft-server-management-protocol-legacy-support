package space.chunks.msmp.shared.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.Player;
import space.chunks.msmp.shared.model.UserBan;

public interface BanOperations {
    CompletableFuture<List<UserBan>> getBans();

    CompletableFuture<List<UserBan>> setBans(List<UserBan> bans);

    CompletableFuture<List<UserBan>> addBans(List<UserBan> bans);

    CompletableFuture<List<UserBan>> removeBans(List<Player> players);

    CompletableFuture<List<UserBan>> clearBans();
}
