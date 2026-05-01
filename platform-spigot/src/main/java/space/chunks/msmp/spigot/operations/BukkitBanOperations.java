package space.chunks.msmp.spigot.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import space.chunks.msmp.spigot.BukkitManagementNotifications;
import space.chunks.msmp.spigot.mapping.BukkitBanMapper;
import space.chunks.msmp.spigot.BukkitPlayerLookup;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.model.UserBan;
import space.chunks.msmp.shared.spi.BanOperations;

final class BukkitBanOperations implements BanOperations {
    private final BukkitServerThreadExecutor executor;
    private final BukkitPlayerLookup players;
    private final BukkitBanMapper banMapper;
    private final BukkitManagementNotifications notifications;

    BukkitBanOperations(BukkitServerThreadExecutor executor, BukkitPlayerLookup players, BukkitBanMapper banMapper, BukkitManagementNotifications notifications) {
        this.executor = executor;
        this.players = players;
        this.banMapper = banMapper;
        this.notifications = notifications;
    }

    @Override
    public CompletableFuture<List<UserBan>> getBans() {
        return executor.call(this::bans);
    }

    @Override
    public CompletableFuture<List<UserBan>> setBans(List<UserBan> bans) {
        return executor.call(() -> {
            BanList list = Bukkit.getBanList(BanList.Type.NAME);
            clear(list);
            add(list, bans);
            return bans();
        });
    }

    @Override
    public CompletableFuture<List<UserBan>> addBans(List<UserBan> bans) {
        return executor.call(() -> {
            add(Bukkit.getBanList(BanList.Type.NAME), bans);
            return bans();
        });
    }

    @Override
    public CompletableFuture<List<UserBan>> removeBans(List<space.chunks.msmp.shared.model.Player> players) {
        return executor.call(() -> {
            BanList list = Bukkit.getBanList(BanList.Type.NAME);
            for (space.chunks.msmp.shared.model.Player player : players) {
                String name = this.players.name(player);
                if (name != null) {
                    list.pardon(name);
                    notifications.publish("bans/removed", player);
                }
            }
            return bans();
        });
    }

    @Override
    public CompletableFuture<List<UserBan>> clearBans() {
        return executor.call(() -> {
            clear(Bukkit.getBanList(BanList.Type.NAME));
            return bans();
        });
    }

    private List<UserBan> bans() {
        List<UserBan> bans = new ArrayList<UserBan>();
        for (BanEntry entry : Bukkit.getBanList(BanList.Type.NAME).getBanEntries()) {
            bans.add(banMapper.userBan(entry));
        }
        return bans;
    }

    private void add(BanList list, List<UserBan> bans) {
        for (UserBan ban : bans) {
            String name = players.name(ban.getPlayer());
            if (name != null) {
                list.addBan(name, ban.getReason(), banMapper.expiration(ban.getExpires()), banMapper.source(ban.getSource()));
                notifications.publish("bans/added", ban);
                kickIfOnline(name);
            }
        }
    }

    private void clear(BanList list) {
        for (BanEntry entry : new ArrayList<BanEntry>(list.getBanEntries())) {
            list.pardon(entry.getTarget());
            notifications.publish("bans/removed", new space.chunks.msmp.shared.model.Player(entry.getTarget(), null));
        }
    }

    private static void kickIfOnline(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            online.kickPlayer("Banned");
        }
    }
}
