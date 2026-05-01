package space.chunks.msmp.shared.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.IncomingIpBan;
import space.chunks.msmp.shared.model.IpBan;

public interface IpBanOperations {
    CompletableFuture<List<IpBan>> getIpBans();

    CompletableFuture<List<IpBan>> setIpBans(List<IpBan> bans);

    CompletableFuture<List<IpBan>> addIpBans(List<IncomingIpBan> bans);

    CompletableFuture<List<IpBan>> removeIpBans(List<String> ips);

    CompletableFuture<List<IpBan>> clearIpBans();
}
