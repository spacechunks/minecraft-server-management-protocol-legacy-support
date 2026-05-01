package space.chunks.msmp.spigot.operations;

import java.net.InetSocketAddress;
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
import space.chunks.msmp.shared.model.IncomingIpBan;
import space.chunks.msmp.shared.model.IpBan;
import space.chunks.msmp.shared.spi.IpBanOperations;

final class BukkitIpBanOperations implements IpBanOperations {
    private final BukkitServerThreadExecutor executor;
    private final BukkitPlayerLookup players;
    private final BukkitBanMapper banMapper;
    private final BukkitManagementNotifications notifications;

    BukkitIpBanOperations(BukkitServerThreadExecutor executor, BukkitPlayerLookup players, BukkitBanMapper banMapper, BukkitManagementNotifications notifications) {
        this.executor = executor;
        this.players = players;
        this.banMapper = banMapper;
        this.notifications = notifications;
    }

    @Override
    public CompletableFuture<List<IpBan>> getIpBans() {
        return executor.call(this::bans);
    }

    @Override
    public CompletableFuture<List<IpBan>> setIpBans(List<IpBan> bans) {
        return executor.call(() -> {
            BanList list = Bukkit.getBanList(BanList.Type.IP);
            clear(list);
            for (IpBan ban : bans) {
                list.addBan(ban.getIp(), ban.getReason(), banMapper.expiration(ban.getExpires()), banMapper.source(ban.getSource()));
                notifications.publish("ip_bans/added", ban);
            }
            return bans();
        });
    }

    @Override
    public CompletableFuture<List<IpBan>> addIpBans(List<IncomingIpBan> bans) {
        return executor.call(() -> {
            BanList list = Bukkit.getBanList(BanList.Type.IP);
            for (IncomingIpBan ban : bans) {
                String ip = ipAddress(ban);
                if (ip != null) {
                    BanEntry entry = list.addBan(ip, ban.getReason(), banMapper.expiration(ban.getExpires()), banMapper.source(ban.getSource()));
                    notifications.publish("ip_bans/added", banMapper.ipBan(entry));
                }
            }
            return bans();
        });
    }

    @Override
    public CompletableFuture<List<IpBan>> removeIpBans(List<String> ips) {
        return executor.call(() -> {
            BanList list = Bukkit.getBanList(BanList.Type.IP);
            for (String ip : ips) {
                list.pardon(ip);
                notifications.publish("ip_bans/removed", ip);
            }
            return bans();
        });
    }

    @Override
    public CompletableFuture<List<IpBan>> clearIpBans() {
        return executor.call(() -> {
            clear(Bukkit.getBanList(BanList.Type.IP));
            return bans();
        });
    }

    private List<IpBan> bans() {
        List<IpBan> bans = new ArrayList<IpBan>();
        for (BanEntry entry : Bukkit.getBanList(BanList.Type.IP).getBanEntries()) {
            bans.add(banMapper.ipBan(entry));
        }
        return bans;
    }

    private String ipAddress(IncomingIpBan ban) {
        if (ban.getIp() != null) {
            return ban.getIp();
        }
        if (ban.getPlayer() == null) {
            return null;
        }
        Player player = players.online(ban.getPlayer());
        InetSocketAddress address = player == null ? null : player.getAddress();
        return address == null ? null : address.getAddress().getHostAddress();
    }

    private void clear(BanList list) {
        for (BanEntry entry : new ArrayList<BanEntry>(list.getBanEntries())) {
            list.pardon(entry.getTarget());
            notifications.publish("ip_bans/removed", entry.getTarget());
        }
    }
}
