package space.chunks.msmp.spigot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public final class BukkitPlayerLookup {
    public OfflinePlayer offline(space.chunks.msmp.shared.model.Player player) {
        if (player.getId() != null) {
            return Bukkit.getOfflinePlayer(player.getId());
        }
        return Bukkit.getOfflinePlayer(player.getName());
    }

    public Player online(space.chunks.msmp.shared.model.Player player) {
        if (player.getId() != null) {
            return Bukkit.getPlayer(player.getId());
        }
        return player.getName() == null ? null : Bukkit.getPlayerExact(player.getName());
    }

    public String name(space.chunks.msmp.shared.model.Player player) {
        if (player == null) {
            return null;
        }
        if (player.getName() != null) {
            return player.getName();
        }
        if (player.getId() != null) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getId());
            return offline == null ? null : offline.getName();
        }
        return null;
    }

    public space.chunks.msmp.shared.model.Player toModel(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        return new space.chunks.msmp.shared.model.Player(player.getName(), id);
    }

    public List<space.chunks.msmp.shared.model.Player> online() {
        List<space.chunks.msmp.shared.model.Player> players = new ArrayList<space.chunks.msmp.shared.model.Player>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(toModel(player));
        }
        return players;
    }

    public List<space.chunks.msmp.shared.model.Player> allowlist() {
        List<space.chunks.msmp.shared.model.Player> players = new ArrayList<space.chunks.msmp.shared.model.Player>();
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            players.add(toModel(player));
        }
        return players;
    }
}
