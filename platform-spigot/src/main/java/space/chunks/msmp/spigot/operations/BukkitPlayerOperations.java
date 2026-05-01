package space.chunks.msmp.spigot.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import space.chunks.msmp.shared.text.PlainTextMessageFormatter;
import space.chunks.msmp.spigot.BukkitPlayerLookup;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.model.KickPlayer;
import space.chunks.msmp.shared.spi.PlayerOperations;

final class BukkitPlayerOperations implements PlayerOperations {
    private final BukkitServerThreadExecutor executor;
    private final BukkitPlayerLookup players;

    BukkitPlayerOperations(BukkitServerThreadExecutor executor, BukkitPlayerLookup players) {
        this.executor = executor;
        this.players = players;
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> getPlayers() {
        return executor.call(players::online);
    }

    @Override
    public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> kickPlayers(List<KickPlayer> players) {
        return executor.call(() -> {
            List<space.chunks.msmp.shared.model.Player> kicked = new ArrayList<space.chunks.msmp.shared.model.Player>();
            for (KickPlayer kick : players) {
                Player player = this.players.online(kick.getPlayer());
                if (player != null) {
                    player.kickPlayer(PlainTextMessageFormatter.format(kick.getMessage()));
                    kicked.add(this.players.toModel(player));
                }
            }
            return kicked;
        });
    }
}
