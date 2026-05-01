package space.chunks.msmp.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import space.chunks.msmp.shared.model.KickPlayer;
import space.chunks.msmp.shared.model.ServerState;
import space.chunks.msmp.shared.model.SystemMessage;
import space.chunks.msmp.shared.model.Version;
import space.chunks.msmp.shared.protocol.MinecraftJavaProtocolVersions;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;
import space.chunks.msmp.shared.spi.PlayerOperations;
import space.chunks.msmp.shared.spi.ServerOperations;
import space.chunks.msmp.shared.spi.UnsupportedManagementOperations;
import space.chunks.msmp.shared.text.PlainTextMessageFormatter;

final class VelocityManagementOperations extends UnsupportedManagementOperations {
    private final ProxyServer proxy;
    private final PlayerOperations players = new VelocityPlayerOperations();
    private final ServerOperations server = new VelocityServerOperations();

    VelocityManagementOperations(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public PlatformCapabilities capabilities() {
        return VelocityManagementCapabilities.create();
    }

    @Override
    public PlayerOperations players() {
        return players;
    }

    @Override
    public ServerOperations server() {
        return server;
    }

    space.chunks.msmp.shared.model.Player toModel(Player player) {
        return new space.chunks.msmp.shared.model.Player(player.getUsername(), player.getUniqueId());
    }

    List<space.chunks.msmp.shared.model.Player> onlinePlayers() {
        List<space.chunks.msmp.shared.model.Player> players = new ArrayList<space.chunks.msmp.shared.model.Player>();
        for (Player player : proxy.getAllPlayers()) {
            players.add(toModel(player));
        }
        return players;
    }

    private Player find(space.chunks.msmp.shared.model.Player player) {
        if (player.getId() != null) {
            return proxy.getPlayer(player.getId()).orElse(null);
        }
        return player.getName() == null ? null : proxy.getPlayer(player.getName()).orElse(null);
    }

    private final class VelocityPlayerOperations implements PlayerOperations {
        @Override
        public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> getPlayers() {
            return CompletableFuture.completedFuture(onlinePlayers());
        }

        @Override
        public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> kickPlayers(List<KickPlayer> players) {
            List<space.chunks.msmp.shared.model.Player> kicked = new ArrayList<space.chunks.msmp.shared.model.Player>();
            for (KickPlayer kick : players) {
                Player player = find(kick.getPlayer());
                if (player != null) {
                    player.disconnect(Component.text(PlainTextMessageFormatter.format(kick.getMessage())));
                    kicked.add(toModel(player));
                }
            }
            return CompletableFuture.completedFuture(kicked);
        }
    }

    private final class VelocityServerOperations implements ServerOperations {
        @Override
        public CompletableFuture<ServerState> status() {
            return CompletableFuture.completedFuture(new ServerState(onlinePlayers(), true, version()));
        }

        @Override
        public CompletableFuture<Boolean> save(boolean flush) {
            return unsupportedServerOperation();
        }

        @Override
        public CompletableFuture<Boolean> stop() {
            proxy.shutdown();
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }

        @Override
        public CompletableFuture<Boolean> sendSystemMessage(SystemMessage message) {
            Component component = Component.text(PlainTextMessageFormatter.format(message.getMessage()));
            if (message.getReceivingPlayers() == null) {
                proxy.sendMessage(component);
                return CompletableFuture.completedFuture(Boolean.TRUE);
            }
            if (message.getReceivingPlayers().isEmpty()) {
                return CompletableFuture.completedFuture(Boolean.FALSE);
            }
            for (space.chunks.msmp.shared.model.Player target : message.getReceivingPlayers()) {
                Player player = find(target);
                if (player != null) {
                    player.sendMessage(component);
                }
            }
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }

        private CompletableFuture<Boolean> unsupportedServerOperation() {
            CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
            future.completeExceptionally(new UnsupportedOperationException("Operation is not supported by this platform"));
            return future;
        }
    }

    Version version() {
        String version = proxy.getVersion().getName() + " " + proxy.getVersion().getVersion();
        int protocol = MinecraftJavaProtocolVersions.protocolForVersionText(version);
        return new Version(protocol, version);
    }
}
